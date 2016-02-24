// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.salesforce.runtime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.talend.components.api.adaptor.Adaptor;
import org.talend.components.api.adaptor.ComponentDynamicHolder;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.component.runtime.WriterResult;
import org.talend.components.api.component.runtime.WriterResult.Type;
import org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputProperties;
import org.talend.daikon.schema.Schema;
import org.talend.daikon.schema.SchemaElement;

import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.UpsertResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

final class SalesforceWriter implements Writer<WriterResult> {

    private SalesforceWriteOperation salesforceWriteOperation;

    private PartnerConnection connection;

    private String uId;

    private SalesforceSink sink;

    private Adaptor adaptor;

    private Map<String, SchemaElement> fieldMap;

    private List<SchemaElement> fieldList;

    private SchemaElement dynamicField;

    private TSalesforceOutputProperties sprops;

    private String upsertKeyColumn;

    protected List<String> deleteItems;

    protected List<SObject> insertItems;

    protected List<SObject> upsertItems;

    protected List<SObject> updateItems;

    protected int commitLevel;

    protected boolean exceptionForErrors;

    /**
     * DOC sgandon SalesforceWriter constructor comment.
     * 
     * @param salesforceWriteOperation
     * @param adaptor
     */
    public SalesforceWriter(SalesforceWriteOperation salesforceWriteOperation, Adaptor adaptor) {
        this.salesforceWriteOperation = salesforceWriteOperation;
        this.adaptor = adaptor;
        sink = (SalesforceSink) salesforceWriteOperation.getSink();
        sprops = sink.getSalesforceOutputProperties();
    }

    @Override
    public void open(String uId) throws IOException {
        this.uId = uId;
        connection = sink.connect();
        Schema schema = sink.getSchema(adaptor, sprops.module.moduleName.getStringValue());
        fieldMap = schema.getRoot().getChildMap();
        fieldList = schema.getRoot().getChildren();

        for (SchemaElement se : fieldList) {
            if (se.getType() == SchemaElement.Type.DYNAMIC) {
                dynamicField = se;
                break;
            }
        }
        upsertKeyColumn = sprops.upsertKeyColumn.getStringValue();

    }

    @Override
    public void write(Object object) throws IOException {
        // todo handle generic type and direct type if we want.
        // of course the following cast is not meant to be there, let wait for Ryan's work
        Map<String, Object> row = (Map<String, Object>) object;
        if (!TSalesforceOutputProperties.ACTION_DELETE.equals(sprops.outputAction.getValue())) {
            SObject so = new SObject();
            so.setType(sprops.module.moduleName.getStringValue());

            for (String key : row.keySet()) {
                Object value = row.get(key);
                if (value != null) {
                    SchemaElement se = fieldMap.get(key);
                    if (se != null && se.getType() != SchemaElement.Type.DYNAMIC) {
                        addSObjectField(so, se, value);
                    }
                }
            }

            if (dynamicField != null) {
                ComponentDynamicHolder dynamic = (ComponentDynamicHolder) row.get(dynamicField.getName());
                List<SchemaElement> dynamicSes = dynamic.getSchemaElements();
                for (SchemaElement dynamicSe : dynamicSes) {
                    Object value = dynamic.getFieldValue(dynamicSe.getName());
                    addSObjectField(so, dynamicSe, value);
                }
            }

            switch (TSalesforceOutputProperties.OutputAction.valueOf(sprops.outputAction.getStringValue())) {
            case INSERT:
                insert(so);
                break;
            case UPDATE:
                update(so);
                break;
            case UPSERT:
                upsert(so);
                break;
            case DELETE:
                // See below
                throw new RuntimeException("Impossible");
            }
        } else { // DELETE
            String id = getIdValue(row);
            if (id != null) {
                delete(id);
            }
        }
    }

    protected String getIdValue(Map<String, Object> row) {
        String ID = "Id";
        if (row.get(ID) != null) {
            SchemaElement se = fieldMap.get(ID);
            if (se.getType() != SchemaElement.Type.DYNAMIC) {
                return (String) row.get(ID);
            }
        }
        // FIXME - need better exception
        if (dynamicField == null) {
            throw new RuntimeException("Expected dynamic column to be available");
        }

        ComponentDynamicHolder dynamic = (ComponentDynamicHolder) row.get(dynamicField.getName());
        List<SchemaElement> dynamicSes = dynamic.getSchemaElements();
        for (SchemaElement dynamicSe : dynamicSes) {
            if (dynamicSe.getName().equals(ID)) {
                return (String) dynamic.getFieldValue(ID);
            }
        }

        // FIXME - need better exception
        throw new RuntimeException(ID + " not found in dynamic columns");
    }

    protected void addSObjectField(SObject sObject, SchemaElement se, Object value) {
        Object valueToAdd;
        switch (se.getType()) {
        case BYTE_ARRAY:
            valueToAdd = Charset.defaultCharset().decode(ByteBuffer.wrap((byte[]) value)).toString();
            break;
        case DATE:
        case DATETIME:
            valueToAdd = adaptor.formatDate((Date) value, se.getPattern());
            break;
        default:
            valueToAdd = value;
            break;
        }
        sObject.setField(se.getName(), valueToAdd);
    }

    protected SaveResult[] insert(SObject sObject) throws IOException {
        insertItems.add(sObject);
        return doInsert();
    }

    protected SaveResult[] doInsert() throws IOException {
        if (insertItems.size() >= commitLevel) {
            SObject[] accs = insertItems.toArray(new SObject[insertItems.size()]);
            String[] changedItemKeys = new String[accs.length];
            SaveResult[] sr;
            try {
                sr = connection.create(accs);
                insertItems.clear();
                if (sr != null && sr.length != 0) {
                    int batch_idx = -1;
                    for (SaveResult result : sr) {
                        handleResults(result.getSuccess(), result.getErrors(), changedItemKeys, ++batch_idx);
                    }
                }
                return sr;
            } catch (ConnectionException e) {
                throw new IOException(e);
            }
        }
        return null;
    }

    protected SaveResult[] update(SObject sObject) throws IOException {
        updateItems.add(sObject);
        return doUpdate();
    }

    protected SaveResult[] doUpdate() throws IOException {
        if (updateItems.size() >= commitLevel) {
            SObject[] upds = updateItems.toArray(new SObject[updateItems.size()]);
            String[] changedItemKeys = new String[upds.length];
            for (int ix = 0; ix < upds.length; ++ix) {
                changedItemKeys[ix] = upds[ix].getId();
            }
            SaveResult[] saveResults;
            try {
                saveResults = connection.update(upds);
                updateItems.clear();
                upds = null;

                if (saveResults != null && saveResults.length != 0) {
                    int batch_idx = -1;
                    for (SaveResult result : saveResults) {
                        handleResults(result.getSuccess(), result.getErrors(), changedItemKeys, ++batch_idx);
                    }
                }
                return saveResults;
            } catch (ConnectionException e) {
                throw new IOException(e);
            }
        }
        return null;
    }

    protected UpsertResult[] upsert(SObject sObject) throws IOException {
        upsertItems.add(sObject);
        return doUpsert();
    }

    protected UpsertResult[] doUpsert() throws IOException {
        if (upsertItems.size() >= commitLevel) {
            SObject[] upds = upsertItems.toArray(new SObject[upsertItems.size()]);
            String[] changedItemKeys = new String[upds.length];
            for (int ix = 0; ix < upds.length; ++ix) {
                Object value = upds[ix].getField(upsertKeyColumn);
                if (value == null) {
                    changedItemKeys[ix] = "No value for " + upsertKeyColumn + " ";
                } else {
                    changedItemKeys[ix] = upsertKeyColumn;
                }
            }
            UpsertResult[] upsertResults;
            try {
                upsertResults = connection.upsert(upsertKeyColumn, upds);
                upsertItems.clear();
                upds = null;

                if (upsertResults != null && upsertResults.length != 0) {
                    int batch_idx = -1;
                    for (UpsertResult result : upsertResults) {
                        handleResults(result.getSuccess(), result.getErrors(), changedItemKeys, ++batch_idx);
                    }
                }
                return upsertResults;
            } catch (ConnectionException e) {
                new IOException(e);
            }
        }
        return null;

    }

    protected void handleResults(boolean success, Error[] resultErrors, String[] changedItemKeys, int batchIdx)
            throws IOException {
        StringBuilder errors = new StringBuilder("");
        if (success) {
            // TODO: send back the ID
        } else {
            errors = addLog(resultErrors,
                    batchIdx < changedItemKeys.length ? changedItemKeys[batchIdx] : "Batch index out of bounds");
        }
        if (exceptionForErrors && errors.toString().length() > 0) {
            throw new IOException(errors.toString());
        }
    }

    protected StringBuilder addLog(Error[] resultErrors, String row_key) {
        StringBuilder errors = new StringBuilder("");
        if (resultErrors != null) {
            for (Error error : resultErrors) {
                errors.append(error.getMessage()).append("\n");
            }
        }
        return errors;
    }

    protected DeleteResult[] delete(String id) throws IOException {
        if (id == null) {
            return null;
        }
        deleteItems.add(id);
        return doDelete();
    }

    protected DeleteResult[] doDelete() throws IOException {
        if (deleteItems.size() >= commitLevel) {
            String[] delIDs = deleteItems.toArray(new String[deleteItems.size()]);
            String[] changedItemKeys = new String[delIDs.length];
            for (int ix = 0; ix < delIDs.length; ++ix) {
                changedItemKeys[ix] = delIDs[ix];
            }
            DeleteResult[] dr;
            try {
                dr = connection.delete(delIDs);
                deleteItems.clear();

                if (dr != null && dr.length != 0) {
                    int batch_idx = -1;
                    for (DeleteResult result : dr) {
                        handleResults(result.getSuccess(), result.getErrors(), changedItemKeys, ++batch_idx);
                    }
                }
                return dr;
            } catch (ConnectionException e) {
                throw new IOException(e);
            }
        }
        return null;
    }

    @Override
    public WriterResult close() throws IOException {
        logout();
        // this should be computed according to the result of the write I guess but I don't know yet how exceptions are
        // handled by Beam.
        return new WriterResult(uId, Type.OK);
    }

    protected void logout() throws IOException {
        // Finish anything uncommitted
        doInsert();
        doDelete();
        doUpdate();
        doUpsert();
    }

    @Override
    public WriteOperation<WriterResult> getWriteOperation() {
        return salesforceWriteOperation;
    }
}
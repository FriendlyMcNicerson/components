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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.talend.components.api.adaptor.Adaptor;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties;
import org.talend.daikon.schema.Schema;
import org.talend.daikon.schema.SchemaElement;

import com.sforce.ws.ConnectionException;

public class SalesforceInputReader extends SalesforceReader {

    protected TSalesforceInputProperties properties;

    protected int commitLevel;

    public SalesforceInputReader(Adaptor adaptor, SalesforceSource source, TSalesforceInputProperties props) {
        super(adaptor, source);
        properties = props;
        commitLevel = props.batchSize.getIntValue();
    }

    @Override
    public boolean start() throws IOException {
        super.start();
        Schema schema = source.getSchema(adaptor, properties.module.moduleName.getStringValue());
        fieldMap = schema.getRoot().getChildMap();
        fieldList = schema.getRoot().getChildren();

        for (SchemaElement se : fieldList) {
            if (se.getType() == SchemaElement.Type.DYNAMIC) {
                dynamicField = se;
                break;
            }
        }

        connection.setQueryOptions(properties.batchSize.getIntValue());

        /*
         * Dynamic columns are requested, find them from Salesforce and only look at the ones that are not explicitly
         * specified in the schema.
         */
        if (dynamicField != null) {
            dynamicFieldMap = new HashMap<>();
            List<SchemaElement> filteredDynamicFields = new ArrayList<>();
            Schema dynSchema = schema;

            for (SchemaElement se : dynSchema.getRoot().getChildren()) {
                if (fieldMap.containsKey(se.getName())) {
                    continue;
                }
                filteredDynamicFields.add(se);
                dynamicFieldMap.put(se.getName(), se);
            }
            dynamicFieldList = filteredDynamicFields;
        }

        inputFieldsToUse = new ArrayList<>();
        for (SchemaElement s : fieldList) {
            if (s.getType() == SchemaElement.Type.DYNAMIC) {
                continue;
            }
            inputFieldsToUse.add(s);
        }
        if (dynamicFieldList != null) {
            inputFieldsToUse.addAll(dynamicFieldList);
        }

        String queryText;
        if (properties.manualQuery.getBooleanValue()) {
            queryText = properties.query.getStringValue();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("select ");
            int count = 0;
            for (SchemaElement se : inputFieldsToUse) {
                if (count++ > 0) {
                    sb.append(", ");
                }
                sb.append(se.getName());
            }
            sb.append(" from ");
            sb.append(properties.module.moduleName.getStringValue());
            queryText = sb.toString();
        }

        try {
            inputResult = connection.query(queryText);
        } catch (ConnectionException e) {
            throw new IOException(e);
        }

        inputRecords = inputResult.getRecords();
        inputRecordsIndex = 0;
        return inputResult.getSize() > 0;
    }

}

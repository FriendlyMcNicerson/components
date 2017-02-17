// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.marketo.tmarketooutput;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.SchemaBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.marketo.MarketoComponentProperties;
import org.talend.components.marketo.helpers.MarketoColumnMappingsTable;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties.APIMode;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

/**
 * Created by undx on 23/01/2017.
 */
public class TMarketoOutputProperties extends MarketoComponentProperties {

    private transient static final Logger LOG = LoggerFactory.getLogger(TMarketoOutputProperties.class);

    public enum Operation {
        syncLead, // This operation requests an insert or update operation for a lead record.
        syncMultipleLeads // This operation requests an insert or update operation for lead records in batch.
    }

    public enum OperationType {
        createOnly,
        updateOnly,
        createOrUpdate,
        createDuplicate
    }

    public enum RESTLookupFields {
        id,
        cookie,
        email,
        twitterId,
        facebookId,
        linkedInId,
        sfdcAccountId,
        sfdcContactId,
        sfdcLeadId,
        sfdcLeadOwnerId,
        sfdcOpptyId
    }

    public Property<Operation> operation = newEnum("operation", Operation.class);

    public Property<OperationType> operationType = newEnum("operationType", OperationType.class);

    public Property<RESTLookupFields> lookupField = newEnum("lookupField", RESTLookupFields.class);

    /*
     * Select this check box to de-duplicate and update lead records using email address. Deselect this check box to
     * create another lead which contains the same email address.
     */
    public Property<Boolean> deDupeEnabled = newBoolean("deDupeEnabled");

    public MarketoColumnMappingsTable mappingInput = new MarketoColumnMappingsTable("mappingInput");
    /*
     * Custom Objects
     * 
     */

    public enum CustomObjectSyncAction {
        createOnly,
        updateOnly,
        createOrUpdate
    }

    public Property<String> customObjectName = newString("customObjectName");

    public Property<CustomObjectSyncAction> customObjectSyncAction = newEnum("customObjectSyncAction",
            CustomObjectSyncAction.class);

    public Property<String> customObjectDedupeBy = newString("customObjectDedupeBy");

    public Property<String> customObjectDeleteBy = newString("customObjectDeleteBy");

    public TMarketoOutputProperties(String name) {
        super(name);
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        Set<PropertyPathConnector> connectors = new HashSet<>();
        if (isOutputConnection) {
            connectors.add(FLOW_CONNECTOR);
            connectors.add(REJECT_CONNECTOR);
        } else {
            connectors.add(MAIN_CONNECTOR);
        }
        return connectors;
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        operation.setPossibleValues(Operation.values());
        operation.setValue(Operation.syncLead);
        operationType.setPossibleValues(OperationType.values());
        operationType.setValue(OperationType.createOnly);

        lookupField.setPossibleValues(RESTLookupFields.values());
        lookupField.setValue(RESTLookupFields.email);

        deDupeEnabled.setValue(false);

        setSchemaListener(new ISchemaListener() {

            @Override
            public void afterSchema() {
                schemaFlow.schema.setValue(null);
                schemaReject.schema.setValue(null);
                updateSchemaRelated();
                refreshLayout(getForm(Form.MAIN));
            }
        });

        // Custom Objects
        customObjectName.setValue("");
        customObjectDedupeBy.setValue("");
        customObjectSyncAction.setPossibleValues(CustomObjectSyncAction.values());
        customObjectSyncAction.setValue(CustomObjectSyncAction.createOrUpdate);
        customObjectDeleteBy.setValue("");
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(operation);
        mainForm.addColumn(operationType);
        mainForm.addRow(lookupField);
        mainForm.addRow(widget(mappingInput).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        mainForm.addRow(deDupeEnabled);
        mainForm.addRow(batchSize);
        mainForm.addRow(dieOnError);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        schemaInput.refreshLayout(schemaInput.getForm(Form.MAIN));
        schemaInput.refreshLayout(schemaInput.getForm(Form.REFERENCE));
        mappingInput.refreshLayout(mappingInput.getForm(Form.MAIN));

        boolean useSOAP = connection.apiMode.getValue().equals(APIMode.SOAP);

        if (form.getName().equals(Form.MAIN)) {
            // first, hide everything
            form.getWidget(mappingInput.getName()).setVisible(false);
            form.getWidget(operationType.getName()).setVisible(false);
            form.getWidget(lookupField.getName()).setVisible(false);
            form.getWidget(deDupeEnabled.getName()).setVisible(false);
            form.getWidget(batchSize.getName()).setVisible(false);
            // batchSize
            if (operation.getValue().equals(Operation.syncMultipleLeads)) {
                form.getWidget(deDupeEnabled.getName()).setVisible(true);
                form.getWidget(batchSize.getName()).setVisible(true);
            }
            //
            if (!useSOAP) {
                form.getWidget(operationType.getName()).setVisible(true);
                form.getWidget(lookupField.getName()).setVisible(true);
                form.getWidget(deDupeEnabled.getName()).setVisible(true);
            }
        }
    }

    public void afterApiMode() {
        if (connection.apiMode.getValue().equals(APIMode.SOAP)) {
            schemaInput.schema.setValue(getSOAPSchemaForSyncLead());
        } else {
            schemaInput.schema.setValue(getRESTSchemaForSyncLead());
        }
        afterOperation();
    }

    public void afterOperation() {
        updateSchemaRelated();
        refreshLayout(getForm(Form.MAIN));
    }

    public void updateSchemaRelated() {
        updateMappings();
        updateOutputSchemas();
    }

    public void updateMappings() {
        List<String> fld = getSchemaFields();
        mappingInput.schemaColumnName.setPossibleValues(fld);
        mappingInput.schemaColumnName.setValue(fld);
        List<String> mcn = new ArrayList<>();
        for (String t : fld)
            mcn.add("");
        mappingInput.marketoColumnName.setValue(mcn);
    }

    protected void updateOutputSchemas() {
        Schema inputSchema = schemaInput.schema.getValue();
        inputSchema.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");

        final List<Field> flowFields = new ArrayList<Field>();
        final List<Field> rejectFields = new ArrayList<Field>();
        Field f;
        f = new Field("Status", Schema.create(Type.STRING), null, (Object) null);
        f.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
        f.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
        flowFields.add(f);
        //
        f = new Field("Status", Schema.create(Type.STRING), null, (Object) null);
        f.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
        f.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
        rejectFields.add(f);
        f = new Field("ERROR_MSG", Schema.create(Schema.Type.STRING), null, (Object) null);
        f.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
        f.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
        rejectFields.add(f);

        Schema flowSchema = newSchema(inputSchema, "schemaFlow", flowFields);
        Schema rejectSchema = newSchema(inputSchema, "schemaReject", rejectFields);
        schemaFlow.schema.setValue(flowSchema);
        schemaReject.schema.setValue(rejectSchema);
    }

    public static Schema getSOAPSchemaForSyncLead() {
        return SchemaBuilder.builder().record("syncLead").fields() //
                .name("Id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().nullable().intType().noDefault() //
                .name("Email").type().nullable().stringType().noDefault() //
                .name("ForeignSysPersonId").type().nullable().stringType().noDefault() //
                .name("ForeignSysType").type().nullable().stringType().noDefault() //
                .endRecord();
    }

    public static Schema getRESTSchemaForSyncLead() {
        return SchemaBuilder.builder().record("syncLead").fields() //
                .name("id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().nullable().intType().noDefault() //
                .name("email").type().nullable().stringType().noDefault() //
                .name("firstName").type().nullable().stringType().noDefault() //
                .name("lastName").type().nullable().stringType().noDefault() //
                .endRecord();
    }

}

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
package org.talend.components.marketo.tmarketolistoperation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.SchemaBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.marketo.MarketoComponentProperties;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties.APIMode;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class TMarketoListOperationProperties extends MarketoComponentProperties {

    public static final String FIELD_ERROR_MSG = "ERROR_MSG";

    public static final String FIELD_STATUS = "Status";

    public static final String FIELD_SUCCESS = "Success";

    public static final String FIELD_LEAD_ID = "LeadId";

    public static final String FIELD_LIST_ID = "ListId";

    public static final String FIELD_LEAD_KEY_VALUE = "LeadKeyValue";

    public static final String FIELD_LEAD_KEY_TYPE = "LeadKeyType";

    public static final String FIELD_LIST_KEY_VALUE = "ListKeyValue";

    public static final String FIELD_LIST_KEY_TYPE = "ListKeyType";

    public enum Operation {
        addTo, // adds one or more leads to a list in the Marketo DB.
        isMemberOf, // checks the Marketo DB to judge whether the specific leads exist in the list.
        removeFrom // removes one or more leads from a list in the Marketo DB.
    }

    private transient static final Logger LOG = LoggerFactory.getLogger(TMarketoListOperationProperties.class);

    public Property<Operation> operation = PropertyFactory.newEnum("operation", Operation.class);

    public Property<Boolean> multipleOperation = PropertyFactory.newBoolean("multipleOperation");

    public TMarketoListOperationProperties(String name) {
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

        schemaInput.schema.setValue(getRESTSchemaMain());
        updateOutputSchemas();
        setSchemaListener(new ISchemaListener() {

            @Override
            public void afterSchema() {
                LOG.debug("afterSchema");
                schemaFlow.schema.setValue(null);
                schemaReject.schema.setValue(null);
                updateOutputSchemas();
                refreshLayout(getForm(Form.MAIN));
            }
        });

        operation.setPossibleValues(Operation.values());
        operation.setValue(Operation.addTo);
        multipleOperation.setValue(false);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(operation);
        mainForm.addRow(multipleOperation);
        mainForm.addRow(dieOnError);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        if (form.getName().equals(Form.MAIN)) {
            switch (operation.getValue()) {
                case addTo :
                case removeFrom :
                    form.getWidget(multipleOperation.getName()).setVisible(true);
                    break;
                default :
                    form.getWidget(multipleOperation.getName()).setVisible(false);
            }
        }
    }

    public void afterApiMode() {
        if (connection.apiMode.getValue().equals(APIMode.SOAP))
            schemaInput.schema.setValue(getSOAPSchemaMain());
        else
            schemaInput.schema.setValue(getRESTSchemaMain());
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterOperation() {
        if (operation.getValue().equals(Operation.isMemberOf))
            multipleOperation.setValue(false);
        refreshLayout(getForm(Form.MAIN));
    }

    public ValidationResult validateMultipleOperation() {
        ValidationResult vr = new ValidationResult();
        if (operation.getValue().equals(Operation.isMemberOf) && multipleOperation.getValue()) {
            vr.setStatus(Result.ERROR);
            vr.setMessage("multipleOperation flag cannot be set with operation=isMemberOf!");
            return vr;
        }
        return ValidationResult.OK;
    }

    public void updateOutputSchemas() {
        Schema inputSchema = schemaInput.schema.getValue();
        inputSchema.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");

        final List<Field> flowFields = new ArrayList<Field>();
        final List<Field> rejectFields = new ArrayList<Field>();
        Field f;
        if (connection.apiMode.getValue().equals(APIMode.SOAP)) {
            f = new Field(FIELD_SUCCESS, Schema.create(Schema.Type.BOOLEAN), null, (Object) null);
            f.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
            f.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
            flowFields.add(f);
            //
            f = new Field(FIELD_ERROR_MSG, Schema.create(Schema.Type.STRING), null, (Object) null);
            f.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
            f.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
            rejectFields.add(f);
        } else {
            f = new Field(FIELD_STATUS, Schema.create(Schema.Type.STRING), null, (Object) null);
            f.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
            f.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
            flowFields.add(f);
            //
            f = new Field(FIELD_STATUS, Schema.create(Schema.Type.STRING), null, (Object) null);
            f.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
            f.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
            rejectFields.add(f);
            f = new Field(FIELD_ERROR_MSG, Schema.create(Schema.Type.STRING), null, (Object) null);
            f.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
            f.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
            rejectFields.add(f);
        }

        Schema flowSchema = newSchema(inputSchema, "schemaFlow", flowFields);
        Schema rejectSchema = newSchema(inputSchema, "schemaReject", rejectFields);
        schemaFlow.schema.setValue(flowSchema);
        schemaReject.schema.setValue(rejectSchema);
    }

    public static Schema getRESTSchemaMain() {
        return SchemaBuilder.builder().record("REST").fields() //
                .name(FIELD_LIST_ID)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().intType().noDefault() //
                //
                .name(FIELD_LEAD_ID)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().intType().noDefault() //
                .endRecord();
    }

    public static Schema getSOAPSchemaMain() {
        return SchemaBuilder.builder().record("SOAP").fields() //
                .name(FIELD_LIST_KEY_TYPE)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().stringType().noDefault() //
                .name(FIELD_LIST_KEY_VALUE)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().stringType().noDefault() //
                .name(FIELD_LEAD_KEY_TYPE)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().stringType().noDefault() //
                .name(FIELD_LEAD_KEY_VALUE)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().stringType().noDefault() //
                .endRecord();
    }

}

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
package org.talend.components.salesforce.tsalesforceinput;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.avro.Schema;
import org.talend.components.api.properties.ComponentPropertyFactory;
import org.talend.components.api.properties.HasSchemaProperty;
import org.talend.components.salesforce.SalesforceConnectionModuleProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.Property.Type;
import org.talend.daikon.properties.presentation.Form;

import java.util.Arrays;
import java.util.List;

import static org.talend.daikon.properties.PropertyFactory.newProperty;

public class TSalesforceInputProperties extends SalesforceConnectionModuleProperties implements HasSchemaProperty {

    public static final String QUERY_QUERY = "Query";

    public static final String QUERY_BULK = "Bulk";

    public Property queryMode = newProperty(Type.ENUM, "queryMode"); //$NON-NLS-1$

    public Property condition = newProperty("condition"); //$NON-NLS-1$

    public Property manualQuery = newProperty(Type.BOOLEAN, "manualQuery"); //$NON-NLS-1$

    public Property query = newProperty("query"); //$NON-NLS-1$

    public Property includeDeleted = newProperty(Type.BOOLEAN, "includeDeleted"); //$NON-NLS-1$

    //
    // Advanced
    //
    public Property batchSize = newProperty(Type.INT, "batchSize"); //$NON-NLS-1$

    public Property normalizeDelimiter = newProperty("normalizeDelimiter"); //$NON-NLS-1$

    public Property columnNameDelimiter = newProperty("columnNameDelimiter"); //$NON-NLS-1$

    public TSalesforceInputProperties(@JsonProperty("name") String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        returns = ComponentPropertyFactory.newReturnsProperty();
        ComponentPropertyFactory.newReturnProperty(returns, Type.INT, "NB_LINE");
        // FIXME - should use default value
        batchSize.setValue(100);

        queryMode.setPossibleValues(QUERY_QUERY, QUERY_BULK);

    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(queryMode);
        mainForm.addRow(condition);
        mainForm.addRow(manualQuery);
        mainForm.addRow(query);
        mainForm.addRow(includeDeleted);

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(batchSize);
        advancedForm.addRow(normalizeDelimiter);
        advancedForm.addRow(columnNameDelimiter);
    }

    public void afterQueryMode() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterManualQuery() {
        refreshLayout(getForm(Form.MAIN));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            form.getWidget(includeDeleted.getName())
                    .setVisible(queryMode.getValue() != null && queryMode.getValue().equals(QUERY_QUERY));

            form.getWidget(query.getName()).setVisible(manualQuery.getBooleanValue());
            form.getWidget(condition.getName()).setVisible(!manualQuery.getBooleanValue());
        }
    }

    @Override
    public List<Schema> getSchemas() {
        return Arrays.asList(new Schema[]{new Schema.Parser().parse(module.schema.schema.getStringValue())});
    }

    @Override
    public void setSchemas(List<Schema> schemas) {
        module.schema.schema.setValue(schemas.get(0));
    }
}

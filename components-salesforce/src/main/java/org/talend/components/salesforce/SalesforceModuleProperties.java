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
package org.talend.components.salesforce;

import static org.talend.components.api.properties.presentation.Widget.*;
import static org.talend.components.api.schema.SchemaFactory.*;

import java.util.ArrayList;
import java.util.List;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.NameAndLabel;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.properties.presentation.Widget;
import org.talend.components.api.schema.SchemaElement;
import org.talend.components.common.SchemaProperties;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("salesforceModuleProperties")
public class SalesforceModuleProperties extends ComponentProperties {

    private SalesforceConnectionProperties connection;

    //
    // Properties
    //
    public SchemaElement moduleName = newProperty("moduleName"); //$NON-NLS-1$

    public SchemaProperties schema = new SchemaProperties("shema"); //$NON-NLS-1$

    // FIXME - OK what about if we are using a connection from a separate component
    // that defines the connection, how do we get that separate component?
    public SalesforceModuleProperties(String name, SalesforceConnectionProperties connectionProperties) {
        super(name);

        connection = connectionProperties;
        setupLayout();
    }

    @Override
    protected void setupLayout() {
        super.setupLayout();

        Form moduleForm = Form.create(this, Form.MAIN, "Salesforce Module");
        moduleForm.addRow(widget(moduleName).setWidgetType(Widget.WidgetType.NAME_SELECTION_AREA));
        refreshLayout(moduleForm);

        Form moduleRefForm = Form.create(this, Form.REFERENCE, "Salesforce Module");
        moduleRefForm.addRow(widget(moduleName).setWidgetType(Widget.WidgetType.NAME_SELECTION_REFERENCE));

        moduleRefForm.addRow(schema.getForm(Form.REFERENCE));
        refreshLayout(moduleRefForm);
    }

    public void beforeModuleName() throws Exception {
        SalesforceRuntime conn = new SalesforceRuntime();
        conn.connect(connection);
        List<NameAndLabel> moduleNames = conn.getModuleNames();
        List<String> possibleValues = new ArrayList<>();
        for (NameAndLabel nl : moduleNames) {
            possibleValues.add(nl.name);
        }
        // FIXME - these are labels, need to have a corresponding actual values.
        // SOmehow have to do this at the widget level
        moduleName.setPossibleValues(possibleValues);
    }

    public void afterModuleName() throws Exception {
        SalesforceRuntime conn = new SalesforceRuntime();
        conn.connect(connection);
        schema.setValue(schema.schema, conn.getSchema(getStringValue(moduleName)));
    }

}

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

import java.util.List;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.NameAndLabel;
import org.talend.components.api.properties.ValidationResult;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.properties.presentation.Widget;
import org.talend.components.api.schema.SchemaElement;
import org.talend.components.common.SchemaProperties;

public class SalesforceModuleProperties extends ComponentProperties {

    private SalesforceConnectionProperties connection;

    //
    // Properties
    //
    public SchemaElement moduleName = newString("moduleName"); //$NON-NLS-1$

    public SchemaProperties schema = new SchemaProperties().init();

    // FIXME - OK what about if we are using a connection from a separate component
    // that defines the connection, how do we get that separate component?
    public SalesforceModuleProperties setConnection(SalesforceConnectionProperties conn) {
        connection = conn;
        return this;
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

    // consider beforeActivate and beforeRender (change after to afterActivate)l

    public void beforeModuleName() throws Exception {
        SalesforceRuntime conn = new SalesforceRuntime();
        ValidationResult vr = conn.connectWithResult(connection);
        if (vr.getStatus() == ValidationResult.Result.OK) {
            List<NameAndLabel> moduleNames = conn.getModuleNames();
            moduleName.setPossibleValues(moduleNames);
        }
    }

    public void afterModuleName() throws Exception {
        SalesforceRuntime conn = new SalesforceRuntime();
        ValidationResult vr = conn.connectWithResult(connection);
        if (vr.getStatus() == ValidationResult.Result.OK) {
            schema.setValue(schema.schema, conn.getSchema(getStringValue(moduleName)));
        }
    }

}

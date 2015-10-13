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

import static org.talend.components.api.properties.presentation.Widget.widget;
import static org.talend.components.api.schema.SchemaFactory.newProperty;

import java.util.List;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.NameAndLabel;
import org.talend.components.api.properties.ValidationResult;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.properties.presentation.Widget;
import org.talend.components.api.schema.Schema;
import org.talend.components.api.schema.SchemaElement;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.internal.ComponentServiceImpl;

public class SalesforceModuleListProperties extends ComponentProperties {

    private SalesforceConnectionProperties connectionProps;

    private String repositoryLocation;

    private List<NameAndLabel> moduleNames;

    //
    // Properties
    //
    public SchemaElement moduleName = newProperty("moduleName").setOccurMaxTimes(-1); //$NON-NLS-1$

    public static final String MAIN = "Main"; //$NON-NLS-1$

    public SalesforceModuleListProperties setConnection(SalesforceConnectionProperties connection) {
        connectionProps = connection;
        return this;
    }

    public SalesforceModuleListProperties setRepositoryLocation(String location) {
        repositoryLocation = location;
        return this;
    }

    @Override
    protected void setupLayout() {
        super.setupLayout();

        Form moduleForm = Form.create(this, MAIN, "Salesforce Modules");
        // Since this is a repeating property it has a list of values
        moduleForm.addRow(widget(moduleName).setWidgetType(Widget.WidgetType.NAME_SELECTION_AREA));
        refreshLayout(moduleForm);
    }

    // For the tests
    public SalesforceConnectionProperties getConnectionProps() {
        return connectionProps;
    }

    public void beforeFormPresentMain() throws Exception {
        SalesforceRuntime conn = new SalesforceRuntime();
        conn.connect(connectionProps);
        moduleNames = conn.getModuleNames();
        setValue(moduleName, moduleNames);
    }

    public void afterFormFinishMain() throws Exception {
        SalesforceRuntime conn = new SalesforceRuntime();
        ValidationResult vr = conn.connectWithResult(connectionProps);
        if (vr.getStatus() != ValidationResult.Result.OK) {
            // FIXME - add error handling, finish can fail
            return;
        }

        ComponentService service = ComponentServiceImpl.TEMP_INSTANCE;

        String connRepLocation = service.storeComponentProperties(connectionProps,
                (String) connectionProps.getValue(connectionProps.name), repositoryLocation, null);

        @SuppressWarnings("unchecked")
        List<NameAndLabel> selectedModuleNames = (List<NameAndLabel>) getValue(moduleName);
        for (NameAndLabel nl : selectedModuleNames) {
            SalesforceModuleProperties modProps = new SalesforceModuleProperties().setConnection(connectionProps);
            Schema schema = conn.getSchema(nl.getName());
            modProps.setValue(modProps.moduleName, nl.getName());
            modProps.schema.setValue(modProps.schema.schema, schema);
            service.storeComponentProperties(modProps, nl.getName(), connRepLocation, schema);
        }

    }

}

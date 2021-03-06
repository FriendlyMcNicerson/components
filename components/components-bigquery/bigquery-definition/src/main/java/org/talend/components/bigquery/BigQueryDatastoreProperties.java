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

package org.talend.components.bigquery;

import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class BigQueryDatastoreProperties extends PropertiesImpl implements DatastoreProperties {

    public Property<String> projectName = PropertyFactory.newString("projectName");

    /**
     * service account need to set on pipeline options, so it's kind of global setting
     * refer to: https://developers.google.com/identity/protocols/OAuth2ServiceAccount
     */
    public Property<String> serviceAccountFile = PropertyFactory.newString("serviceAccountFile");

    /** temp gs folder need to set on pipeline options, so it's kind of global setting */
    public Property<String> tempGsFolder = PropertyFactory.newString("tempGsFolder");

    public BigQueryDatastoreProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(projectName);
        mainForm.addRow(Widget.widget(serviceAccountFile).setWidgetType(Widget.FILE_WIDGET_TYPE));
        mainForm.addRow(tempGsFolder);
    }

}

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

import java.util.ArrayList;
import java.util.List;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.PresentationItem;
import org.talend.components.api.properties.ValidationResult;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.properties.presentation.Widget.WidgetType;
import org.talend.components.api.schema.SchemaElement;
import org.talend.components.common.ProxyProperties;
import org.talend.components.common.UserPasswordProperties;
import org.talend.components.common.oauth.OauthProperties;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("salesforceConnectionProperties")
public class SalesforceConnectionProperties extends ComponentProperties {

    public static final String URL = "https://www.salesforce.com/services/Soap/u/34.0";
    public static final String OAUTH_URL =
             "https://login.salesforce.com/services/oauth2";


    //
    // Properties
    //

    // Only for the wizard use
    public SchemaElement name = newProperty("name").setRequired(true);

    public static final String LOGIN_BASIC = "Basic";

    public static final String LOGIN_OAUTH = "OAuth";

    public SchemaElement loginType = newProperty(Type.ENUM, "loginType").setRequired(true);

    public SchemaElement bulkConnection = newProperty(SchemaElement.Type.BOOLEAN, "bulkConnection"); //$NON-NLS-1$

    public SchemaElement needCompression = newProperty(SchemaElement.Type.BOOLEAN, "needCompression"); //$NON-NLS-1$

    public SchemaElement timeout = newProperty(SchemaElement.Type.INT, "timeout"); //$NON-NLS-1$

    public SchemaElement httpTraceMessage = newProperty("httpTraceMessage"); //$NON-NLS-1$

    public SchemaElement clientId = newProperty("clientId"); //$NON-NLS-1$

    //
    // Presentation items
    //
    public PresentationItem testConnection = new PresentationItem("testConnection", "Test connection");

    public PresentationItem advanced = new PresentationItem("advanced", "Advanced...");

    //
    // Nested property collections
    //
    public OauthProperties oauth = new OauthProperties("oauth"); //$NON-NLS-1$

    public UserPasswordProperties userPassword = new UserPasswordProperties("userPassword"); //$NON-NLS-1$

    public ComponentProperties proxy = new ProxyProperties("proxy"); //$NON-NLS-1$

    public static final boolean INCLUDE_NAME = true;

    public SalesforceConnectionProperties(String name) {
        this(name, !INCLUDE_NAME);
    }

    public SalesforceConnectionProperties(String name, boolean includeName) {
        super(name);
        if (!includeName)
            name = null;
        List<String> loginTypes = new ArrayList<>();
        loginTypes.add(LOGIN_BASIC);
        loginTypes.add(LOGIN_OAUTH);
        loginType.setPossibleValues(loginTypes);
        setupLayout();
    }

    @Override
    protected void setupLayout() {
        super.setupLayout();

        setValue(loginType, LOGIN_BASIC);

        Form connectionForm = Form.create(this, Form.MAIN, getI18nMessage("property.form.Main.title"));
        connectionForm.setSubtitle(getI18nMessage("property.form.Main.subtitle"));

        if (name != null) {
            connectionForm.addRow(name);
        }

        connectionForm.addRow(widget(loginType).setDeemphasize(true));

        // Only one of these is visible at a time
        connectionForm.addRow(oauth.getForm(Form.MAIN));
        connectionForm.addRow(userPassword.getForm(Form.MAIN));

        connectionForm.addRow(widget(advanced).setWidgetType(WidgetType.BUTTON));
        connectionForm.addColumn(widget(testConnection).setLongRunning(true).setWidgetType(WidgetType.BUTTON));
        refreshLayout(connectionForm);

        Form advancedForm = Form.create(this, Form.ADVANCED, getI18nMessage("property.form.Advanced.title"));
        advancedForm.addRow(bulkConnection);
        advancedForm.addRow(needCompression);
        advancedForm.addRow(httpTraceMessage);
        advancedForm.addRow(clientId);
        advancedForm.addRow(timeout);
        advancedForm.addRow(proxy.getForm(Form.MAIN));
        advanced.setFormtoShow(advancedForm);
        refreshLayout(advancedForm);
    }

    public void afterLoginType() {
        refreshLayout(getForm(Form.MAIN));
    }

    public ValidationResult validateTestConnection() throws Exception {
        SalesforceRuntime conn = new SalesforceRuntime();
        return conn.connectWithResult(this);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            if (LOGIN_OAUTH.equals(getValue(loginType))) {
                form.getWidget("oauth").setVisible(true);
                form.getWidget("userPassword").setVisible(false);
            } else if (LOGIN_BASIC.equals(getValue(loginType))) {
                form.getWidget("oauth").setVisible(false);
                form.getWidget("userPassword").setVisible(true);
            } else {
                throw new RuntimeException("Enum value should be handled :" + getValue(loginType));
            }
        }
    }

}

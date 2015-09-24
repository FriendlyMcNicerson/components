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
import java.util.Collections;
import java.util.List;

import org.talend.components.api.i18n.I18nMessageProvider;
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

    //
    // Properties
    //

    // public String apiVersion;

    // Only for the wizard use
    public SchemaElement name = newProperty("name");

    public SchemaElement url = newProperty("url").setRequired(true); //$NON-NLS-1$

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
    public PresentationItem connectionDesc = new PresentationItem("connectionDesc",
            "Complete these fields in order to connect to your Salesforce account");

    public PresentationItem testConnection = new PresentationItem("testConnection", "Test connection");

    public PresentationItem advanced = new PresentationItem("advanced", "Advanced...");

    //
    // Nested property collections
    //
    public OauthProperties oauth;

    public UserPasswordProperties userPassword;

    public ComponentProperties proxy;

    public static final String MAIN = "Main";

    public static final String ADVANCED = "Advanced";

    public SalesforceConnectionProperties(I18nMessageProvider i18nMessageProvider) {
        this(i18nMessageProvider, !INCLUDE_NAME); //$NON-NLS-1$
    }

    public static final boolean INCLUDE_NAME = true;

    public SalesforceConnectionProperties(I18nMessageProvider i18nMessageProvider, boolean includeName) {
        super(i18nMessageProvider, "org.talend.components.salesforce.message"); //$NON-NLS-1$

        if (!includeName)
            name = null;

        List loginTypes = new ArrayList<>();
        loginTypes.add(LOGIN_BASIC);
        loginTypes.add(LOGIN_OAUTH);
        loginType.setPossibleValues(loginTypes);

        oauth = new OauthProperties(i18nMessageProvider);
        userPassword = new UserPasswordProperties(i18nMessageProvider);
        proxy = new ProxyProperties(i18nMessageProvider);
        setupLayout();
        setupPropertiesWithI18n();
    }

    @Override protected void setupLayout() {
        super.setupLayout();

        setValue(loginType, LOGIN_BASIC);

        Form connectionForm = Form.create(this, MAIN, "Salesforce Connection Settings");
        connectionForm.addRow(connectionDesc);

        if (name != null)
            connectionForm.addRow(name);

        connectionForm.addRow(widget(loginType).setDeemphasize(true));

        // Only one of these is visible at a time
        connectionForm.addRow(oauth.getForm(OauthProperties.OAUTH));
        connectionForm.addRow(userPassword.getForm(UserPasswordProperties.USERPASSWORD));

        connectionForm.addRow(widget(advanced).setWidgetType(WidgetType.BUTTON));
        connectionForm.addColumn(widget(testConnection).setLongRunning(true).setWidgetType(WidgetType.BUTTON));

        refreshLayout(connectionForm);

        Form advancedForm = Form.create(this, ADVANCED, "Advanced Connection Settings");
        advancedForm.addRow(bulkConnection);
        advancedForm.addRow(needCompression);
        advancedForm.addRow(httpTraceMessage);
        advancedForm.addRow(clientId);
        advancedForm.addRow(timeout);
        advancedForm.addRow(url);
        advancedForm.addRow(proxy.getForm(ProxyProperties.PROXY));
        refreshLayout(advancedForm);
    }

    public void afterLoginType() {
        refreshLayout(getForm(MAIN));
    }

    public ValidationResult validateTestConnection() throws Exception {
        SalesforceRuntime conn = new SalesforceRuntime();
        return conn.connectWithResult(this);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(MAIN)) {
            if (LOGIN_OAUTH.equals(getValue(loginType))) {
                form.getWidget(OauthProperties.OAUTH).setVisible(true);
                setValue(url, "https://login.salesforce.com/services/oauth2");
                form.getWidget(UserPasswordProperties.USERPASSWORD).setVisible(false);
            } else if (LOGIN_BASIC.equals(getValue(loginType))) {
                form.getWidget(OauthProperties.OAUTH).setVisible(false);
                setValue(url, "https://www.salesforce.com/services/Soap/u/34.0");
                form.getWidget(UserPasswordProperties.USERPASSWORD).setVisible(true);
            } else {
                throw new RuntimeException("Enum value should be handled :" + getValue(loginType));
            }
        }
    }

}

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

import com.fasterxml.jackson.annotation.JsonRootName;
import org.talend.components.api.ComponentProperties;
import org.talend.components.api.properties.PresentationItem;
import org.talend.components.api.properties.Property;
import org.talend.components.api.properties.ValidationResult;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.properties.presentation.Layout.WidgetType;
import org.talend.components.api.properties.presentation.Wizard;
import org.talend.components.common.CommonProperties;
import org.talend.components.common.ProxyProperties;
import org.talend.components.common.UserPasswordProperties;
import org.talend.components.common.oauth.OauthProperties;

import static org.talend.components.api.properties.presentation.Layout.layout;

@JsonRootName("salesforceConnectionProperties") public class SalesforceConnectionProperties extends ComponentProperties {

    public SalesforceConnectionProperties() {
        super();
        setupLayout();
    }

    //
    // Properties
    //

    // public String apiVersion;
    public Property<String> url = new Property<String>("url", "Salesforce URL").setRequired(true) //$NON-NLS-1$//$NON-NLS-2$
            .setValue("https://www.salesforce.com/services/Soap/u/34.0"); //$NON-NLS-1$

    public enum LoginType {
        BASIC,
        OAUTH
    }

    public Property<LoginType> loginType = new Property<LoginType>("loginType", "Connection type").setRequired(true)
            .setValue(LoginType.BASIC);

    public Property<Boolean> bulkConnection = new Property<Boolean>("bulkConnection", "Bulk Connection");

    public Property<Boolean> needCompression = new Property<Boolean>("needCompression", "Need compression");

    public Property<Integer> timeout = new Property<Integer>("timeout", "Timeout").setValue(0);

    public Property<Boolean> httpTraceMessage = new Property<Boolean>("httpTraceMessage", "Trace HTTP message");

    public Property<String> clientId = new Property<String>("clientId", "Client Id");

    public Property<ProxyProperties> proxy = new Property<ProxyProperties>("proxy", "Proxy").setValue(new ProxyProperties());

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
    public CommonProperties common = new CommonProperties();

    public OauthProperties oauth = new OauthProperties();

    public UserPasswordProperties userPassword = new UserPasswordProperties();

    public static final String MAIN = "Main";

    public static final String ADVANCED = "Advanced";

    @Override protected void setupLayout() {
        super.setupLayout();

        Form connectionForm = Form.create(this, MAIN, "Salesforce Connection Settings");
        connectionForm.addChild(connectionDesc, layout().setRow(1));

        connectionForm.addChild(common.getForm(CommonProperties.MAIN), layout().setRow(2));
        connectionForm.addChild(loginType, layout().setRow(3).setDeemphasize(true));

        // Only one of these is visible at a time
        connectionForm.addChild(oauth.getForm(OauthProperties.OAUTH), layout().setRow(4));
        connectionForm.addChild(userPassword.getForm(UserPasswordProperties.USERPASSWORD), layout().setRow(4));

        connectionForm.addChild(url, layout().setRow(5));

        connectionForm.addChild(advanced, layout().setRow(6).setOrder(1).setWidgetType(WidgetType.BUTTON));
        connectionForm
                .addChild(testConnection, layout().setRow(6).setOrder(2).setLongRunning(true).setWidgetType(WidgetType.BUTTON));
        refreshLayout(connectionForm);

        Form advancedForm = Form.create(this, ADVANCED, "Advanced Connection Settings");
        advancedForm.addChild(bulkConnection, layout().setRow(1));
        advancedForm.addChild(needCompression, layout().setRow(2));
        advancedForm.addChild(httpTraceMessage, layout().setRow(3));
        advancedForm.addChild(clientId, layout().setRow(4));
        advancedForm.addChild(timeout, layout().setRow(5));
        advancedForm.addChild(proxy, layout().setRow(5));
        refreshLayout(advancedForm);

        Wizard wizard = Wizard.create(this, "Connection", "Salesforce Connection");
        // TODO - need to set the icon for the wizard
        wizard.addForm(connectionForm);
    }

    public void afterLoginType() {
        refreshLayout(getForm(MAIN));
    }

    public ValidationResult validateTestConnection() throws Exception {
        SalesforceRuntime conn = new SalesforceRuntime();
        conn.connect(this);
        // FIXME - handle the error catching
        return new ValidationResult();
    }

    @Override public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(MAIN)) {
            switch (loginType.getValue()) {
            case OAUTH:
                form.getLayout(OauthProperties.OAUTH).setVisible(true);
                form.getLayout(UserPasswordProperties.USERPASSWORD).setVisible(false);
                break;
            case BASIC:
                form.getLayout(OauthProperties.OAUTH).setVisible(false);
                form.getLayout(UserPasswordProperties.USERPASSWORD).setVisible(true);
                break;
            default:
                throw new RuntimeException("Enum value should be handled :" + loginType.getValue());
            }
        }
    }

}

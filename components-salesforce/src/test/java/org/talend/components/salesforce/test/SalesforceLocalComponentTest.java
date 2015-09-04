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
package org.talend.components.salesforce.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.talend.components.api.ComponentProperties;
import org.talend.components.api.ComponentService;
import org.talend.components.api.properties.Property;
import org.talend.components.api.internal.SpringApp;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.common.oauth.OauthProperties;
import org.talend.components.common.UserPasswordProperties;
import org.talend.components.salesforce.SalesforceConnectionProperties;
import org.talend.components.salesforce.SalesforceModuleProperties;
import org.talend.components.salesforce.tsalesforceconnect.TSalesforceConnectDefinition;
import org.talend.components.salesforce.tsalesforceconnect.TSalesforceConnectProperties;

import junit.framework.TestCase;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputDefinition;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringApp.class)
public class SalesforceLocalComponentTest extends TestCase {

    @Autowired protected ComponentService componentService;

    public SalesforceLocalComponentTest() {
    }

    protected ComponentProperties checkAndBefore(Form form, String propName, ComponentProperties props) throws Throwable {
        assertTrue(form.getLayout(propName).isCallBefore());
        return componentService.beforeProperty(propName, props);
    }

    protected ComponentProperties checkAndAfter(Form form, String propName, ComponentProperties props) throws Throwable {
        assertTrue(form.getLayout(propName).isCallAfter());
        return componentService.afterProperty(propName, props);
    }

    protected ComponentProperties checkAndValidate(Form form, String propName, ComponentProperties props) throws Throwable {
        assertTrue(form.getLayout(propName).isCallValidate());
        return componentService.validateProperty(propName, props);
    }

    @Test public void testGetProps() {
        ComponentProperties props = componentService.getComponentProperties(TSalesforceConnectDefinition.COMPONENT_NAME);
        Form f = props.getForm(TSalesforceConnectProperties.CONNECTION);
        System.out.println(f);
        System.out.println(props);
    }

    @Test public void testAfterLoginType() throws Throwable {
        SalesforceConnectionProperties props;
        Form f;

        props = (SalesforceConnectionProperties) componentService
                .getComponentProperties(TSalesforceConnectDefinition.COMPONENT_NAME);
        assertEquals(SalesforceConnectionProperties.LoginType.BASIC, props.loginType.getValue());
        f = props.getForm(TSalesforceConnectProperties.CONNECTION);
        assertTrue(f.getLayout(UserPasswordProperties.USERPASSWORD).isVisible());
        assertFalse(f.getLayout(OauthProperties.OAUTH).isVisible());

        props.loginType.setValue(SalesforceConnectionProperties.LoginType.OAUTH);
        props = (SalesforceConnectionProperties) checkAndAfter(f, "loginType", props);
        f = props.getForm(TSalesforceConnectProperties.CONNECTION);
        assertTrue(f.isRefreshUI());

        assertFalse(f.getLayout(UserPasswordProperties.USERPASSWORD).isVisible());
        assertTrue(f.getLayout(OauthProperties.OAUTH).isVisible());
    }

    private SalesforceConnectionProperties setupProps(SalesforceConnectionProperties props) {
        if (props == null) {
            props = (SalesforceConnectionProperties) componentService
                    .getComponentProperties(TSalesforceConnectDefinition.COMPONENT_NAME);
        }
        System.out.println("URI:" + props.url.getValue());
        props.userPassword.userId.setValue("fupton@talend.com");
        props.userPassword.password.setValue("talendsal99QSCzLBQgrkEq9w9EXiOt1BSy");
        return props;
    }

    @Test public void testLogin() throws Throwable {
        SalesforceConnectionProperties props = setupProps(null);
        Form f = props.getForm(TSalesforceConnectProperties.CONNECTION);
        props = (SalesforceConnectionProperties) checkAndValidate(f, "testConnection", props);
        System.out.println(props.getValidationResult());
    }

    @Test public void testBulkLogin() throws Throwable {
        SalesforceConnectionProperties props = setupProps(null);
        props.bulkConnection.setValue(true);
        Form f = props.getForm(TSalesforceConnectProperties.CONNECTION);
        props = (SalesforceConnectionProperties) checkAndValidate(f, "testConnection", props);
        System.out.println(props.getValidationResult());
    }

    @Test public void testModuleNames() throws Throwable {
        TSalesforceInputProperties props = (TSalesforceInputProperties) componentService
                .getComponentProperties(TSalesforceInputDefinition.COMPONENT_NAME);
        setupProps(props.connection);

        assertEquals(2, props.getForms().size());
        assertEquals(SalesforceConnectionProperties.CONNECTION, props.getForms().get(0).getName());
        assertEquals(SalesforceModuleProperties.MODULE, props.getForms().get(1).getName());

        Form f = props.getForm(SalesforceModuleProperties.MODULE);
        assertTrue(f.getLayout("moduleName").isCallBefore());
        // The Form is bound to a Properties object that created it. The Forms might not always be associated with the properties object
        // they came from.
        ComponentProperties moduleProps = f.getProperties();
        moduleProps = (SalesforceModuleProperties) checkAndBefore(f, "moduleName", moduleProps);
        Property prop = (Property) f.getChild("moduleName");
        assertTrue(prop.getPossibleValues().size() > 100);
        System.out.println(prop.getPossibleValues());
        System.out.println(moduleProps.getValidationResult());
    }

}

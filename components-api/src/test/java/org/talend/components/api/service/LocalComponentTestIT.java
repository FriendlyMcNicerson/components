package org.talend.components.api.service;
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

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.components.api.ComponentTestUtils;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.service.testcomponent.TestComponentDefinition;
import org.talend.components.api.service.testcomponent.TestComponentProperties;
import org.talend.components.api.service.testcomponent.TestComponentWizard;
import org.talend.components.api.service.testcomponent.TestComponentWizardDefinition;
import org.talend.components.api.service.testcomponent.nestedprop.NestedComponentProperties;
import org.talend.components.api.wizard.ComponentWizard;
import org.talend.components.api.wizard.WizardImageType;
import org.talend.components.test.SpringApp;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringApp.class)
public class LocalComponentTestIT extends AbstractComponentTestIT {

    @Test
    public void testSupportsProps() throws Throwable {
        ComponentProperties props = componentService.getComponentProperties(TestComponentDefinition.COMPONENT_NAME);
        List<ComponentDefinition> comps = componentService.getPossibleComponents(props);
        assertEquals("TestComponent", comps.get(0).getName());
        props = new NestedComponentProperties("props");
        comps = componentService.getPossibleComponents(props);
        assertEquals(0, comps.size());
    }

    @Test
    public void testBefore() throws Throwable {
        ComponentProperties props = componentService.getComponentProperties(TestComponentDefinition.COMPONENT_NAME);

        checkAndBeforePresent(props.getForm(Form.MAIN), "nameList", props);
        assertEquals(3, props.getProperty("nameList").getPossibleValues().size());
        assertEquals("name1", props.getProperty("nameList").getPossibleValues().get(0));

        checkAndBeforeActivate(props.getForm(Form.MAIN), "nameListRef", props);
        assertEquals(3, props.getProperty("nameListRef").getPossibleValues().size());
        assertEquals("namer1", props.getProperty("nameListRef").getPossibleValues().get(0));

        assertFalse(props.getForm(Form.MAIN).getWidget("nameList").isCallBeforeActivate());
        assertFalse(props.getForm(Form.MAIN).getWidget("nameListRef").isCallBeforePresent());
    }

    @Test
    // TCOMP-15 Handle OK/Cancel button on advanced properties dialog from Wizard
    public void testFormOkCancel() throws Throwable {
        TestComponentProperties props = (TestComponentProperties) componentService
                .getComponentProperties(TestComponentDefinition.COMPONENT_NAME);

        ComponentProperties savedProps = props;
        ComponentProperties savedNested = props.nestedProps;

        Date dateNow = new Date();
        dateNow.setTime(System.currentTimeMillis());
        Date dateLater = new Date();
        dateLater.setTime(dateLater.getTime() + 10000);

        props.setValue(props.userId, "userId");
        props.setValue(props.integer, 1);
        props.setValue(props.decimal, 2);
        props.setValue(props.date, dateNow);
        props.setValue(props.dateTime, dateNow);
        props.nestedProps.setValue(props.nestedProps.aGreatProperty, "propPrevious1");
        props.nestedProps.setValue(props.nestedProps.anotherProp, "propPrevious2");

        props = (TestComponentProperties) componentService.makeFormCancelable(props, "restoreTest");

        Form form = props.getForm("restoreTest");

        form.setValue("userId", "userIdnew");
        form.setValue("nestedProps.aGreatProperty", "propPrevious1new");

        Date dateTimeLater = new Date();

        form.setValue("integer", 10);
        form.setValue("decimal", 20);
        form.setValue("date", dateLater);
        form.setValue("dateTime", dateLater);

        assertEquals("userId", props.getValue(props.userId));
        assertEquals("propPrevious1", props.nestedProps.getValue(props.nestedProps.aGreatProperty));
        assertEquals(1, props.getIntValue(props.integer));
        // FIXME - finish this
        //assertEquals(2, props.getDecimalValue(props.decimal));
        //assertEquals(dateNow, props.getCalendarValue(props.date));
        assertTrue(props == savedProps);
        assertTrue(props.nestedProps == savedNested);

        props = (TestComponentProperties) componentService.commitFormValues(props, "restoreTest");
        assertEquals("userIdnew", props.getValue(props.userId));
        assertEquals("propPrevious1new", props.nestedProps.getValue(props.nestedProps.aGreatProperty));
    }

    @Test
    public void testGetWizardIconOk() {
        InputStream iconStream = componentService.getWizardPngImage(TestComponentWizardDefinition.COMPONENT_WIZARD_NAME,
                WizardImageType.TREE_ICON_16X16);
        assertNotNull(iconStream);
    }

    @Test(expected = ComponentException.class)
    public void testGetWizardIconWrongName() {
        InputStream iconStream = componentService.getWizardPngImage("not an existing wizard name",
                WizardImageType.TREE_ICON_16X16);
        assertNull(iconStream);
    }

    @Test
    public void testGetWizard() {
        ComponentWizard wizard = componentService.getComponentWizard(TestComponentWizardDefinition.COMPONENT_WIZARD_NAME,
                "userdata");
        assertTrue(wizard instanceof TestComponentWizard);
        assertEquals("userdata", wizard.getRepositoryLocation());
    }

    @Test(expected = ComponentException.class)
    public void testGetWizardNotFound() {
        componentService.getComponentWizard("not found", "userdata");
    }

    @Test
    public void testGetWizardWithProps() {
        TestComponentWizard wizard = (TestComponentWizard) componentService
                .getComponentWizard(TestComponentWizardDefinition.COMPONENT_WIZARD_NAME, "userdata");
        wizard.props = new TestComponentProperties("props").init();
        ComponentProperties props = wizard.props;
        List<ComponentWizard> wizards = componentService.getComponentWizardsForProperties(props, "userdata");
        assertTrue(props == ((TestComponentWizard) wizards.get(0)).props);
    }

    @Test
    public void testGetDependencies() {
        // check the comp def return the proper stream for the pom
        TestComponentDefinition testComponentDefinition = new TestComponentDefinition();
        assertNotNull(testComponentDefinition.getMavenPom());
        Set<String> mavenUriDependencies = componentService.getMavenUriDependencies(TestComponentDefinition.COMPONENT_NAME);
        assertEquals(5, mavenUriDependencies.size());
    }

    @Test
    public void testFamilies() {
        TestComponentDefinition testComponentDefinition = new TestComponentDefinition();
        assertEquals(2, testComponentDefinition.getFamilies().length);
    }

    @Test
    public void testAlli18n() {
        ComponentTestUtils.testAlli18n(componentService);
    }

}

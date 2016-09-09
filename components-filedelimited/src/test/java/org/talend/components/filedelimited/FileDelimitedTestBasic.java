package org.talend.components.filedelimited;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.internal.ComponentServiceImpl;
import org.talend.components.api.test.AbstractComponentTest;
import org.talend.components.api.test.SimpleComponentRegistry;
import org.talend.components.filedelimited.runtime.FileDelimitedSource;
import org.talend.components.filedelimited.tFileInputDelimited.TFileInputDelimitedDefinition;
import org.talend.components.filedelimited.tFileOutputDelimited.TFileOutputDelimitedDefinition;
import org.talend.components.filedelimited.wizard.FileDelimitedWizardDefinition;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("nls")
public class FileDelimitedTestBasic extends AbstractComponentTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDelimitedTestBasic.class);

    public static Schema BASIC_SCHEMA = SchemaBuilder.builder().record("Schema").fields() //
            .name("TestBoolean").type().booleanType().noDefault() //
            .name("TestByte").type(AvroUtils._byte()).noDefault() //
            .name("TestBytes").type(AvroUtils._bytes()).noDefault() //
            .name("TestChar").type(AvroUtils._character()).noDefault() //
            .name("TestDate").prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss")//
            .type(AvroUtils._date()).noDefault() //
            .name("TestDouble").type().doubleType().noDefault() //
            .name("TestFloat").type().floatType().noDefault() //
            .name("TestBigDecimal").type(AvroUtils._decimal()).noDefault()//
            .name("TestInteger").type().intType().noDefault() //
            .name("TestLong").type().longType().noDefault() //
            .name("TestObject").type(AvroUtils._bytes()).noDefault().endRecord();

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private ComponentServiceImpl componentService;

    @Before
    public void initializeComponentRegistryAndService() {
        // reset the component service
        componentService = null;
    }

    @Override
    public ComponentService getComponentService() {
        if (componentService == null) {
            SimpleComponentRegistry testComponentRegistry = new SimpleComponentRegistry();

            testComponentRegistry.addComponent(TFileInputDelimitedDefinition.COMPONENT_NAME, new TFileInputDelimitedDefinition());
            testComponentRegistry.addComponent(TFileOutputDelimitedDefinition.COMPONENT_NAME,
                    new TFileOutputDelimitedDefinition());

            FileDelimitedWizardDefinition wizardDefinition = new FileDelimitedWizardDefinition();
            testComponentRegistry.addWizard(FileDelimitedWizardDefinition.COMPONENT_WIZARD_NAME, wizardDefinition);
            componentService = new ComponentServiceImpl(testComponentRegistry);
        }
        return componentService;
    }

    protected ComponentProperties checkAndAfter(Form form, String propName, ComponentProperties props) throws Throwable {
        assertTrue(form.getWidget(propName).isCallAfter());
        ComponentProperties afterProperty = (ComponentProperties) getComponentService().afterProperty(propName, props);
        assertEquals(
                "ComponentProperties after failed[" + props.getClass().getCanonicalName() + "/after"
                        + StringUtils.capitalize(propName) + "] :" + afterProperty.getValidationResult().getMessage(),
                ValidationResult.Result.OK, afterProperty.getValidationResult().getStatus());
        return afterProperty;
    }

    static public FileDelimitedProperties setupProps(FileDelimitedProperties props) {
        if (props == null) {
            props = (FileDelimitedProperties) new FileDelimitedProperties("foo").init();
        }
        return props;
    }

    protected List<IndexedRecord> readRows(FileDelimitedProperties inputProps) throws IOException {
        FileDelimitedSource source = new FileDelimitedSource();
        source.initialize(null, inputProps);
        source.validate(null);
        BoundedReader<IndexedRecord> reader = source.createReader(null);
        boolean hasRecord = reader.start();
        List<IndexedRecord> rows = new ArrayList<>();
        while (hasRecord) {
            org.apache.avro.generic.IndexedRecord unenforced = reader.getCurrent();
            rows.add(unenforced);
            hasRecord = reader.advance();
        }
        reader.close();
        return rows;
    }

    protected Date parseToDate(String pattern, String strDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.parse(strDate);
    }

}

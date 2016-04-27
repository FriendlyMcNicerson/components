package org.talend.components.splunk;

import java.util.Collections;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.ComponentPropertyFactory;
import org.talend.components.api.properties.HasSchemaProperty;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.PropertyFactory;
import org.talend.daikon.properties.presentation.Form;

/**
 * The ComponentProperties subclass provided by a component stores the 
 * configuration of a component and is used for:
 * 
 * <ol>
 * <li>Specifying the format and type of information (properties) that is 
 *     provided at design-time to configure a component for run-time,</li>
 * <li>Validating the properties of the component at design-time,</li>
 * <li>Containing the untyped values of the properties, and</li>
 * <li>All of the UI information for laying out and presenting the 
 *     properties to the user.</li>
 * </ol>
 * 
 * The tSplunkEventCollectorProperties has two properties:
 * <ol>
 * <li>{code filename}, a simple property which is a String containing the 
 *     file path that this component will read.</li>
 * <li>{code schema}, an embedded property referring to a Schema.</li>
 * </ol>
 */
public class TSplunkEventCollectorProperties extends ComponentProperties implements HasSchemaProperty {

    public static String RESPONSE_CODE_NAME = "RESPONSE_CODE";
    public static String ERROR_MESSAGE_NAME = "ERROR_MESSAGE";

    public Property fullUrl = PropertyFactory.newString("fullUrl"); //$NON-NLS-1$
    public SchemaProperties schema = new SchemaProperties("schema"); //$NON-NLS-1$
    public Property token = PropertyFactory.newString("token");
    public Property eventsBatchSize = PropertyFactory.newInteger("eventsBatchSize");
    public Property extendedOutput = PropertyFactory.newBoolean("extendedOutput");
    public Property RESPONSE_CODE;
    public Property ERROR_MESSAGE;

    public TSplunkEventCollectorProperties(String name) {
        super(name);
        eventsBatchSize.setValue(100);
        extendedOutput.setValue(true);
    }
    
    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = new Form(this, Form.MAIN); //$NON-NLS-1$
        form.addRow(schema.getForm(Form.REFERENCE));
        form.addRow(fullUrl);
        form.addRow(token);
        
        Form advanced = new Form(this, Form.ADVANCED);
        advanced.addRow(extendedOutput);
        advanced.addColumn(eventsBatchSize);
    }
    
    @Override
    public void setupProperties() {
        super.setupProperties();
        
        returns = ComponentPropertyFactory.newReturnsProperty();
        RESPONSE_CODE = ComponentPropertyFactory.newReturnProperty(returns, Property.Type.INT, RESPONSE_CODE_NAME); //$NON-NLS-1$
        ERROR_MESSAGE = ComponentPropertyFactory.newReturnProperty(returns, Property.Type.STRING, ERROR_MESSAGE_NAME); //$NON-NLS-1$
    }

    public int getBatchSize() {
        if(extendedOutput.getBooleanValue()) {
            return eventsBatchSize.getIntValue();
        }
        return 1;
    }
    
    public void afterExtendedOutput() {
        getForm(Form.ADVANCED).getWidget(eventsBatchSize.getName()).setVisible(extendedOutput.getBooleanValue());
    }
    
    @Override
    public List<Schema> getSchemas() {
        return Collections.singletonList(new Schema.Parser().parse(schema.schema.getStringValue()));
    }

    @Override
    public void setSchemas(List<Schema> schemas) {
        this.schema.schema.setValue(schemas.get(0));
    }
    
}

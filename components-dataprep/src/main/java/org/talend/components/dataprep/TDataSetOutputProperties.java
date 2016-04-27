package org.talend.components.dataprep;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.PropertyFactory;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

import java.util.Collections;
import java.util.Set;

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
 * The TDataSetOutputProperties has two properties:
 * <ol>
 * <li>{code dataSetName}, a simple property which is a String containing the
 *     file path that this component will read.</li>
 * <li>{code schema}, an embedded property referring to a Schema.</li>
 * </ol>
 */
public class TDataSetOutputProperties extends FixedConnectorsComponentProperties {

    public Property dataSetName = PropertyFactory.newString("dataSetName");
    public Property login = PropertyFactory.newString("login");
    public Property pass = PropertyFactory.newString("pass");
    public Property url = PropertyFactory.newString("url");
    public Property mode = PropertyFactory.newEnum("mode", "create", "update", "create&update");
    public SchemaProperties schema = new SchemaProperties("schema");
    protected transient PropertyPathConnector MAIN_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "schema");

    public TDataSetOutputProperties(String name) {
        super(name);
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean b) {
        return Collections.singleton(MAIN_CONNECTOR);
    }
    
    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN, "DataSet Output");
        form.addRow(schema.getForm(Form.REFERENCE));
        form.addRow(url);
        form.addRow(login);
        form.addRow(Widget.widget(pass).setWidgetType(Widget.WidgetType.HIDDEN_TEXT));
        form.addRow(mode);
        form.addRow(dataSetName);
    }

    public Schema getSchema() {
        return (Schema) schema.schema.getValue();
    }
}

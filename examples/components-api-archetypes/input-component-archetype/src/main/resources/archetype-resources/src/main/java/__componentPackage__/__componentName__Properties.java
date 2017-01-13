#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package}.${componentPackage};
import static org.talend.daikon.avro.SchemaConstants.TALEND_IS_LOCKED;

import java.util.Collections;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field.Order;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.property.StringProperty;

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
 * The ${componentName}Properties has two properties:
 * <ol>
 * <li>{code filename}, a simple property which is a String containing the 
 *     file path that this component will read.</li>
 * <li>{code schema}, an embedded property referring to a Schema.</li>
 * </ol>
 */
public class ${componentName}Properties extends FixedConnectorsComponentProperties {
	
    private static final Schema LINE_SCHEMA;

    public StringProperty filename = PropertyFactory.newString("filename"); //$NON-NLS-1$
	
    /**
     * Design schema of input component. Design schema defines data fields which should be retrieved from Data Store.
     * In this component example Data Store is a single file on file system 
     */
    public SchemaProperties schema = new SchemaProperties("schema"); //$NON-NLS-1$
	
    protected transient PropertyPathConnector mainConnector = new PropertyPathConnector(Connector.MAIN_NAME, "schema"); //$NON-NLS-1$
	
    /**
     * In this case user can't retrieve any other data from file as lines. So, component specifies default schema here - <code>LINE_SCHEMA</code>
     * Also this schema should not be editable. So, special property <code>TALEND_IS_LOCKED</code> is added to specify that runtime environment should
     * not allow to edit this schema
     */
    static {
        Schema stringSchema = AvroUtils._string();
        Schema.Field lineField = new Schema.Field("line", stringSchema, null, (Object) null, Order.ASCENDING);
        LINE_SCHEMA = Schema.createRecord("file", null, null, false, Collections.singletonList(lineField));
        LINE_SCHEMA.addProp(TALEND_IS_LOCKED, "true");
    }
 
    public ${componentName}Properties(String name) {
        super(name);
    }

    /**
     * Default properties values are set in this method
     */
    @Override
    public void setupProperties() {
        super.setupProperties();
        schema.schema.setValue(LINE_SCHEMA);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN);
        form.addRow(schema.getForm(Form.REFERENCE));
        form.addRow(Widget.widget(filename).setWidgetType(Widget.FILE_WIDGET_TYPE));
    }

    /**
     * Returns input or output component connectors
     * 
     * @param isOutputConnectors specifies what connectors to return, true if output connectors are requires, false if input connectors are requires
     * @return component connectors
     */
    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnectors) {
        if (isOutputConnectors) {
            return Collections.singleton(mainConnector);
        }
        return Collections.emptySet();
    }

}

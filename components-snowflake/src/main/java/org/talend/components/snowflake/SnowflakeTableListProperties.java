package org.talend.components.snowflake;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.List;

import org.apache.avro.Schema;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.snowflake.runtime.SnowflakeSourceOrSink;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.service.Repository;

public class SnowflakeTableListProperties extends ComponentPropertiesImpl implements SnowflakeProvideConnectionProperties {

    public SnowflakeConnectionProperties connection = new SnowflakeConnectionProperties("connection");

    private String repositoryLocation;

    private List<NamedThing> tableNames;

    //
    // Properties
    //
    public Property<List<NamedThing>> selectedTableNames = newProperty(new TypeLiteral<List<NamedThing>>() {
    }, "selectedTableNames"); //$NON-NLS-1$

    public SnowflakeTableListProperties(String name) {
        super(name);
    }

    public SnowflakeTableListProperties setConnection(SnowflakeConnectionProperties connection) {
        this.connection = connection;
        return this;
    }

    public SnowflakeTableListProperties setRepositoryLocation(String location) {
        repositoryLocation = location;
        return this;
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form tableForm = Form.create(this, Form.MAIN);
        // Since this is a repeating property it has a list of values
        tableForm.addRow(widget(selectedTableNames).setWidgetType(Widget.NAME_SELECTION_AREA_WIDGET_TYPE));
        refreshLayout(tableForm);
    }

    // For the tests
    public SnowflakeConnectionProperties getConnectionProps() {
        return connection;
    }

    public void beforeFormPresentMain() throws Exception {
        if (true)
            throw new RuntimeException("fixme");

        //tableNames = SnowflakeSourceOrSink.getSchemaNames(null, this);
        selectedTableNames.setPossibleValues(tableNames);
        getForm(Form.MAIN).setAllowBack(true);
        getForm(Form.MAIN).setAllowFinish(true);
    }

    public ValidationResult afterFormFinishMain(Repository<Properties> repo) throws Exception {
        ValidationResult vr = SnowflakeSourceOrSink.validateConnection(this);
        if (vr.getStatus() != ValidationResult.Result.OK) {
            return vr;
        }

        String connRepLocation = repo.storeProperties(connection, connection.name.getValue(), repositoryLocation, null);

        for (NamedThing nl : selectedTableNames.getValue()) {
            String tableId = nl.getName();
            SnowflakeTableProperties tableProps = new SnowflakeTableProperties(tableId);
            if (true)
                throw new RuntimeException("fixme");
            //tableProps.connectionTable = connection;
            tableProps.init();
            Schema schema = SnowflakeSourceOrSink.getSchema(null, this, tableId);
            tableProps.tableName.setValue(tableId);
            tableProps.main.schema.setValue(schema);
            repo.storeProperties(tableProps, nl.getName(), connRepLocation, "main.schema");
        }
        return ValidationResult.OK;
    }

    @Override
    public SnowflakeConnectionProperties getConnectionProperties() {
        return connection;
    }
}
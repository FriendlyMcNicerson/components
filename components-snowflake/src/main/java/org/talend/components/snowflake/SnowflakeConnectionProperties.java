package org.talend.components.snowflake;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.api.properties.ComponentReferencePropertiesEnclosing;
import org.talend.components.common.UserPasswordProperties;
import org.talend.components.snowflake.runtime.SnowflakeSourceOrSink;
import org.talend.components.snowflake.tsnowflakeconnection.TSnowflakeConnectionDefinition;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

public class SnowflakeConnectionProperties extends ComponentPropertiesImpl
        implements SnowflakeProvideConnectionProperties, ComponentReferencePropertiesEnclosing {

    private static final String USERPASSWORD = "userPassword";

    public static final String FORM_WIZARD = "Wizard";

    // Only for the wizard use
    public Property<String> name = newString("name").setRequired();

    public enum Tracing {
        OFF("OFF"),
        SEVERE("SEVERE"),
        WARNING("WARNING"),
        INFO("INFO"),
        CONFIG("CONFIG"),
        FINE("FINE"),
        FINER("FINER"),
        FINEST("FINEST"),
        ALL("ALL");

        String value;

        private Tracing(String val) {
            this.value = val;
        }
    }

    // Ref: https://docs.snowflake.net/manuals/user-guide/jdbc-configure.html
    public UserPasswordProperties userPassword = new UserPasswordProperties(USERPASSWORD);

    public Property<String> account = newString("account").setRequired(); //$NON-NLS-1$


    public Property<String> warehouse = newString("warehouse"); //$NON-NLS-1$

    public Property<String> db = newString("db"); //$NON-NLS-1$

    public Property<String> schemaName = newString("schemaName"); //$NON-NLS-1$



    public Property<String> role = newString("role"); //$NON-NLS-1$

    public Property<Tracing> tracing = newEnum("tracing", Tracing.class); //$NON-NLS-1$

    // Presentation items
    public PresentationItem testConnection = new PresentationItem("testConnection", "Test connection");

    public PresentationItem advanced = new PresentationItem("advanced", "Advanced...");

    // protected transient PropertyPathConnector mainConnector = new PropertyPathConnector(Connector.MAIN_NAME, "schema");

    public ComponentReferenceProperties referencedComponent = new ComponentReferenceProperties("referencedComponent", this);

    public SnowflakeConnectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        // Code for property initialization goes here

        tracing.setValue(Tracing.OFF);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form wizardForm = Form.create(this, FORM_WIZARD);
        wizardForm.addRow(name);
        wizardForm.addRow(userPassword.getForm(Form.MAIN));
        wizardForm.addRow(account);
        wizardForm.addRow(warehouse);
        wizardForm.addRow(db);
        wizardForm.addRow(schemaName);
        wizardForm.addRow(widget(advanced).setWidgetType(Widget.BUTTON_WIDGET_TYPE));
        wizardForm.addColumn(widget(testConnection).setLongRunning(true).setWidgetType(Widget.BUTTON_WIDGET_TYPE));

        Form mainForm = Form.create(this, Form.MAIN);
        mainForm.addRow(userPassword.getForm(Form.MAIN));
        mainForm.addRow(account);

        Form advancedForm = Form.create(this, Form.ADVANCED);
        advancedForm.addRow(widget(tracing).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        advancedForm.addRow(role);
        advanced.setFormtoShow(advancedForm);

        // form.addRow(schema.getForm(Form.REFERENCE));
        // form.addRow(filename);

        // A form for a reference to a connection, used in a tSnowflakeInput for example
        Form refForm = Form.create(this, Form.REFERENCE);
        Widget compListWidget = widget(referencedComponent).setWidgetType(Widget.COMPONENT_REFERENCE_WIDGET_TYPE);
        referencedComponent.componentType.setValue(TSnowflakeConnectionDefinition.COMPONENT_NAME);
        refForm.addRow(compListWidget);
        refForm.addRow(mainForm);
    }

    @Override
    public void afterReferencedComponent() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
        refreshLayout(getForm(Form.ADVANCED));
    }

    // TODO: refreshLayout (currently handled in super)
    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        String refComponentIdValue = getReferencedComponentId();
        boolean useOtherConnection = refComponentIdValue != null
                && refComponentIdValue.startsWith(TSnowflakeConnectionDefinition.COMPONENT_NAME);
        if (form.getName().equals(Form.MAIN) || form.getName().equals(FORM_WIZARD)) {
            if (useOtherConnection) {
                form.getWidget(USERPASSWORD).setHidden(true);
                form.getWidget(account.getName()).setHidden(true);
            } else {
                // Do nothing
                form.setHidden(false);
                form.getWidget(USERPASSWORD).setHidden(false);
                form.getWidget(account.getName()).setHidden(false);
            }
        }

        if (form.getName().equals(Form.ADVANCED)) {
            if (useOtherConnection) {
                form.setHidden(true);
            } else {
                form.setHidden(false);
                // TODO: check if anything else needs to be handled
            }
        }
    }

    public ValidationResult validateTestConnection() throws Exception {
        ValidationResult vr = SnowflakeSourceOrSink.validateConnection(this);
        if (vr.getStatus() == ValidationResult.Result.OK) {
            vr.setMessage("Connection successful");
            getForm(FORM_WIZARD).setAllowForward(true);
        } else {
            getForm(FORM_WIZARD).setAllowForward(false);
        }
        return vr;
    }

    @Override
    public SnowflakeConnectionProperties getConnectionProperties() {
        return this;
    }

    /*
     * @Override
     * protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputComponent) {
     * if (isOutputComponent) {
     * return Collections.singleton(mainConnector);
     * }
     * return Collections.emptySet();
     * }
     */

    public String getReferencedComponentId() {
        return referencedComponent.componentInstanceId.getStringValue();
    }

    public SnowflakeConnectionProperties getReferencedConnectionProperties() {
        SnowflakeConnectionProperties refProps = (SnowflakeConnectionProperties) referencedComponent.componentProperties;
        if (refProps != null) {
            return refProps;
        }
        return null;
    }

}

package org.talend.components.api.component;

import org.talend.components.api.AbstractTopLevelDefinition;
import org.talend.components.api.properties.ComponentProperties;

public abstract class AbstractComponentDefinition extends AbstractTopLevelDefinition implements ComponentDefinition {

    private ComponentConnector[] connectors;

    public void setConnectors(ComponentConnector... conns) {
        this.connectors = conns;
    }

    protected Class<?> propertiesClass;

    @Override
    public ComponentConnector[] getConnectors() {
        return connectors;
    }

    @Override
    protected String getI18nPrefix() {
        return "component."; //$NON-NLS-1$
    }

    @Override
    public ComponentProperties createProperties() {
        ComponentProperties compProp = null;
        try {
            compProp = (ComponentProperties) propertiesClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        compProp.init();
        return compProp;
    }

    public boolean supportsProperties(ComponentProperties properties) {
        return propertiesClass.isAssignableFrom(properties.getClass());
    }

    //
    // DI Flags - default definitions
    //

    public boolean isSchemaAutoPropagate() {
        return false;
    }

    public boolean isDataAutoPropagate() {
        return false;
    }

    public boolean isConditionalInputs() {
        return false;
    }

    public boolean isStartable() {
        return false;
    }

    public static final String AUTO = "Auto";

    public static final String NONE = "None";

    public String getPartitioning() {
        return null;
    }

}

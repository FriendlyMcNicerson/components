package org.talend.components.netsuite.connection;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.netsuite.TNetSuiteComponentDefinition;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 *
 */
public class TNetSuiteConnectionDefinition extends TNetSuiteComponentDefinition {

    public static final String COMPONENT_NAME = "tNetSuiteConnection_DEV"; //$NON-NLS-1$

    public TNetSuiteConnectionDefinition() {
        super(COMPONENT_NAME, ExecutionEngine.DI);
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return NetSuiteConnectionProperties.class;
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_ERROR_MESSAGE_PROP };
    }

    @Override
    public boolean isStartable() {
        return true;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties,
            ConnectorTopology connectorTopology) {
        assertEngineCompatibility(engine);
        assertConnectorTopologyCompatibility(connectorTopology);
        return getRuntimeInfo(TNetSuiteConnectionDefinition.SOURCE_OR_SINK_CLASS);
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.NONE);
    }

}


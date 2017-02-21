package org.talend.components.marketo.runtime;

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.marketo.MarketoProvideConnectionProperties;
import org.talend.components.marketo.runtime.client.MarketoClientService;
import org.talend.components.marketo.runtime.client.MarketoRESTClient;
import org.talend.components.marketo.runtime.client.MarketoSOAPClient;
import org.talend.components.marketo.runtime.client.type.MarketoException;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties.APIMode;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;

public class MarketoSourceOrSink implements SourceOrSink, MarketoSourceOrSinkSchemaProvider {

    protected MarketoProvideConnectionProperties properties;

    protected MarketoClientService client;

    protected static final String KEY_CONNECTION = "ConnectionProperties";

    private transient static final Logger LOG = LoggerFactory.getLogger(MarketoSourceOrSink.class);

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (MarketoProvideConnectionProperties) properties;
        return ValidationResult.OK;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        TMarketoConnectionProperties conn = connect(container);
        String endpoint = conn.endpoint.getValue();
        String clientAccess = conn.clientAccessId.getValue();
        String secretKey = conn.secretKey.getValue();
        ValidationResult vr = new ValidationResult();
        if (endpoint == null || endpoint.isEmpty()) {
            vr.setMessage("The endpoint cannot be empty.");
            vr.setStatus(ValidationResult.Result.ERROR);
            return vr;
        }
        if (clientAccess == null || clientAccess.isEmpty()) {
            vr.setMessage("The client access ID cannot be empty.");
            vr.setStatus(ValidationResult.Result.ERROR);
            return vr;
        }
        if (secretKey == null || secretKey.isEmpty()) {
            vr.setMessage("The secret key cannot be empty.");
            vr.setStatus(ValidationResult.Result.ERROR);
            return vr;
        }
        return ValidationResult.OK;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    public static ValidationResult validateConnection(MarketoProvideConnectionProperties properties) {
        ValidationResult vr = new ValidationResult().setStatus(Result.OK);
        try {
            MarketoSourceOrSink sos = new MarketoSourceOrSink();
            sos.initialize(null, (ComponentProperties) properties);
            sos.getClientService(null);
        } catch (IOException e) {
            vr.setStatus(Result.ERROR);
            vr.setMessage(e.getLocalizedMessage());
        }
        return vr;
    }

    public TMarketoConnectionProperties connect(RuntimeContainer container) {
        TMarketoConnectionProperties connProps = getConnectionProperties();
        String refComponentId = connProps.getReferencedComponentId();
        TMarketoConnectionProperties sharedConn;
        // Using another component's connection
        if (refComponentId != null) {
            // In a runtime container
            if (container != null) {
                sharedConn = (TMarketoConnectionProperties) container.getComponentData(refComponentId, KEY_CONNECTION);
                if (sharedConn != null) {
                    return sharedConn;
                }
            }
            // Design time
            connProps = connProps.getReferencedConnectionProperties();
            if (connProps == null)
                LOG.warn("Referenced component: {} does not have properties set!", refComponentId);
        }
        //
        if (container != null) {
            container.setComponentData(container.getCurrentComponentId(), KEY_CONNECTION, connProps);
        }
        return connProps;
    }

    public TMarketoConnectionProperties getEffectiveConnectionProperties(RuntimeContainer container) {
        TMarketoConnectionProperties connProps = properties.getConnectionProperties();
        String refComponentId = connProps.getReferencedComponentId();
        // Using another component's connection
        if (refComponentId != null) {
            // In a runtime container
            if (container != null) {
                return (TMarketoConnectionProperties) container.getComponentData(refComponentId, KEY_CONNECTION);
            }
            // Design time
            return connProps.getReferencedConnectionProperties();
        }
        return connProps;
    }

    public TMarketoConnectionProperties getConnectionProperties() {
        return properties.getConnectionProperties();
    }

    public MarketoClientService getClientService(RuntimeContainer container) throws IOException {
        if (client == null) {
            TMarketoConnectionProperties conn = connect(container);
            if (conn == null) {
                conn = getEffectiveConnectionProperties(container);
            }
            try {
                if (conn.apiMode.getValue().equals(APIMode.SOAP)) {
                    client = new MarketoSOAPClient(conn);
                } else {
                    client = new MarketoRESTClient(conn);
                }
            } catch (MarketoException e) {
                LOG.error(e.toString());
                throw new IOException(e);
            }
            LOG.debug("ClientService : {}", client);
        }
        return client;
    }

    @Override
    public Schema getSchemaForParams(ComponentProperties params) {
        LOG.info("getSchemaForParams {}", params);
        return null;
    }

}

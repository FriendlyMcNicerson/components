package org.talend.components.marketo.runtime;

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.marketo.MarketoComponentProperties;
import org.talend.components.marketo.runtime.client.MarketoClientService;
import org.talend.components.marketo.runtime.client.MarketoRESTClient;
import org.talend.components.marketo.runtime.client.MarketoSOAPClient;
import org.talend.components.marketo.runtime.client.type.MarketoException;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties.APIMode;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

public class MarketoSourceOrSink implements SourceOrSink, MarketoSourceOrSinkSchemaProvider {

    private transient static final Logger LOG = LoggerFactory.getLogger(MarketoSourceOrSink.class);

    protected MarketoComponentProperties properties;

    private MarketoClientService client;

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        return ValidationResult.OK;
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (MarketoComponentProperties) properties;

        return ValidationResult.OK;
    }

    public MarketoClientService getClientService() throws IOException {
        if (client == null) {
            try {
                if (properties.connection.apiMode.getValue().equals(APIMode.SOAP)) {
                    client = new MarketoSOAPClient(properties);
                } else {
                    client = new MarketoRESTClient(properties);
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

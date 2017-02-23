package org.talend.components.netsuite.client;

import java.util.Properties;

import org.talend.components.netsuite.test.TestFixture;
import org.talend.components.netsuite.test.TestUtils;

import static org.talend.components.netsuite.client.NetSuiteClientService.MESSAGE_LOGGING_ENABLED_PROPERTY_NAME;

/**
 *
 */
public class NetSuiteWebServiceTestFixture implements TestFixture {

    protected Properties properties;
    protected NetSuiteCredentials credentials;
    protected NetSuiteClientService clientService;

    @Override
    public void setUp() throws Exception {
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");

        String configurationLocation = System.getProperty(
                "netsuite.configuration.location", "/sandbox.properties");
        properties = TestUtils.loadPropertiesFromLocation(configurationLocation);

        credentials = NetSuiteCredentials.loadFromProperties(properties, "credentials.");

        clientService = NetSuiteClientService.create("2016.2");
        clientService.setEndpointUrl(getEndpointUrl());
        clientService.setCredentials(credentials);

        boolean messageLoggingEnabled = Boolean.valueOf(
                System.getProperty(MESSAGE_LOGGING_ENABLED_PROPERTY_NAME, "false"));
        clientService.setMessageLoggingEnabled(messageLoggingEnabled);
    }

    @Override
    public void tearDown() throws Exception {

    }

    public Properties getProperties() {
        return properties;
    }

    public NetSuiteCredentials getCredentials() {
        return credentials;
    }

    public String getEndpointUrl() {
        return properties.getProperty("endpoint.url");
    }

    public NetSuiteClientService getClientService() {
        return clientService;
    }
}

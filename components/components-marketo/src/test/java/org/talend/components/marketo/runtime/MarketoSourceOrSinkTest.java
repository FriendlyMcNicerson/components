package org.talend.components.marketo.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties.APIMode;
import org.talend.components.marketo.tmarketoinput.TMarketoInputProperties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;

public class MarketoSourceOrSinkTest {

    MarketoSourceOrSink sos;

    @Before
    public void setUp() throws Exception {
        sos = new MarketoSourceOrSink();
    }

    @Test
    public void getSchemaNames() throws Exception {
        assertNull(sos.getSchemaNames(null));
    }

    @Test
    public void getEndpointSchema() throws Exception {
        assertNull(sos.getEndpointSchema(null, "schemaInput"));
    }

    @Test
    public void validate() throws Exception {
        TMarketoInputProperties props = new TMarketoInputProperties("test");
        sos.initialize(null, props);
        assertEquals(Result.ERROR, sos.validate(null).getStatus());
    }

    @Test
    public void initialize() throws Exception {
        TMarketoInputProperties props = new TMarketoInputProperties("test");
        assertEquals(ValidationResult.OK, sos.initialize(null, props));
    }

    @Test(expected = IOException.class)
    public void testGetClientService() throws Exception {
        TMarketoInputProperties props = new TMarketoInputProperties("test");
        props.setupProperties();
        props.connection.setupProperties();
        sos.initialize(null, props);
        assertEquals("Marketo REST API Client [].", sos.getClientService(null).toString());
        assertEquals("Marketo REST API Client [].", sos.getClientService(null).toString());// 2times for cache
        props.setupProperties();
        sos = new MarketoSourceOrSink();
        props.connection.apiMode.setValue(APIMode.SOAP);
        props.connection.endpoint.setValue("https://www.marketo.com");
        sos.initialize(null, props);
        assertEquals("Marketo SOAP API Client [null].", sos.getClientService(null).toString());
    }
}

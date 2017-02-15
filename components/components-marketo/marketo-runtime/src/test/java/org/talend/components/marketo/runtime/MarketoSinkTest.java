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
package org.talend.components.marketo.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.marketo.tmarketolistoperation.TMarketoListOperationProperties;
import org.talend.components.marketo.tmarketooutput.TMarketoOutputProperties;
import org.talend.daikon.properties.ValidationResult;

public class MarketoSinkTest {

    MarketoSink sink;
    @Before
    public void setUp() throws Exception {
        sink = new MarketoSink();
    }

    @Test
    public void testValidate() throws Exception {
        assertEquals(ValidationResult.OK, sink.validate(null));
    }

    @Test
    public void testCreateWriteOperation() throws Exception {
        assertNull(new MarketoSink().createWriteOperation());
        sink.properties = new TMarketoListOperationProperties("test");
        assertTrue(sink.createWriteOperation() instanceof MarketoListOperationWriteOperation);
        sink.properties = new TMarketoOutputProperties("test");
        assertTrue(sink.createWriteOperation() instanceof MarketoOutputWriteOperation);
    }

}
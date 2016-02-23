// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.salesforce.runtime;

import static org.junit.Assert.*;
import static org.talend.components.salesforce.SalesforceTestHelper.*;

import java.io.IOException;

import org.junit.Test;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.salesforce.SalesforceConnectionProperties;
import org.talend.components.salesforce.SalesforceTestHelper;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties;

public class SalesforceInputReaderTestIT {

    public BoundedReader createSalesforceInputReaderFromAccount(String moduleName) {
        TSalesforceInputProperties tsip = (TSalesforceInputProperties) new TSalesforceInputProperties("foo").init(); //$NON-NLS-1$
        SalesforceConnectionProperties conProps = setupProps(tsip.connection, !ADD_QUOTES);
        tsip.module.moduleName.setValue(moduleName);
        SalesforceSource salesforceSource = new SalesforceSource();
        salesforceSource.initialize(null, tsip);
        return salesforceSource.createReader(null);
    }

    /**
     * Test method for {@link org.talend.components.salesforce.runtime.SalesforceInputReader#start()}.
     * 
     * @throws IOException
     */
    @Test
    public void testStartAdvanceGetCurrent() throws IOException {
        BoundedReader salesforceInputReader = createSalesforceInputReaderFromAccount(SalesforceTestHelper.EXISTING_MODULE_NAME);
        try {
            assertTrue(salesforceInputReader.start());
            assertTrue(salesforceInputReader.advance());
            assertNotNull(salesforceInputReader.getCurrent());
        } finally {
            salesforceInputReader.close();
        }
    }

    /**
     * Test method for {@link org.talend.components.salesforce.runtime.SalesforceInputReader#start()}.
     * 
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void testStartException() throws IOException {
        BoundedReader salesforceInputReader = createSalesforceInputReaderFromAccount(
                SalesforceTestHelper.NOT_EXISTING_MODULE_NAME);
        try {
            assertTrue(salesforceInputReader.start());
        } finally {
            salesforceInputReader.close();
        }
    }

}

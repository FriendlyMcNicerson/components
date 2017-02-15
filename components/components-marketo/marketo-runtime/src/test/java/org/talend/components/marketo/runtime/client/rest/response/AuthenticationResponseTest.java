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
package org.talend.components.marketo.runtime.client.rest.response;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationResponseTest {

    AuthenticationResponse r;
    @Before
    public void setUp() throws Exception {
        r = new AuthenticationResponse();
        r.setAccess_token("token");
        r.setError("error");
        r.setExpires_in(600);
        r.setScope("REST");
        r.setError_description("desc");
        r.setToken_type("cred");
    }

    @Test
    public void testGetAttr() throws Exception {
        assertEquals("token", r.getAccess_token());
        assertEquals("cred", r.getToken_type());
        assertEquals("error", r.getError());
        assertEquals("desc", r.getErrorDescription());
        assertEquals(600, r.getExpires_in());
        assertEquals("REST", r.getScope());
    }
}
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
package org.talend.components.api.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.components.api.ComponentTestUtils;

public class SchemaTest {

    @BeforeClass
    public static void init() {
        ComponentTestUtils.setupGlobalContext();
    }

    @AfterClass
    public static void unset() {
        ComponentTestUtils.unsetGlobalContext();
    }

    @Test
    public void testSerializeSchema() {
        Schema s = SchemaFactory.newSchema();
        SchemaElement root = s.setRoot(SchemaFactory.newSchemaElement("root"));
        root.addChild(SchemaFactory.newSchemaElement("c1"));
        String ser = s.toSerialized();

        Schema s2 = SchemaFactory.fromSerialized(ser);
        assertEquals("root", s2.getRoot().getName());
        assertNotNull(s2.getRoot().getChild("c1"));
    }

}

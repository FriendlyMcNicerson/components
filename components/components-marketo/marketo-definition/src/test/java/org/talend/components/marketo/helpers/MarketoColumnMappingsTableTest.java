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
package org.talend.components.marketo.helpers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MarketoColumnMappingsTableTest {

    MarketoColumnMappingsTable mappings;

    @Before
    public void setup() {
        mappings = new MarketoColumnMappingsTable("test");
        mappings.setupProperties();
    }

    @Test
    public void testGetNameMappingsForMarketoEmpty() throws Exception {
        assertEquals(Collections.emptyMap(), mappings.getNameMappingsForMarketo());
        List<String> schema = new ArrayList<String>();
        List<String> mkto = new ArrayList<String>();
        mappings.schemaColumnName.setValue(schema);
        mappings.marketoColumnName.setValue(mkto);
        assertEquals(Collections.emptyMap(), mappings.getNameMappingsForMarketo());
    }

    @Test
    public void testGetNameMappingsForMarketoNoMktoMappings() throws Exception {
        List<String> schema = new ArrayList<String>(Arrays.asList("ID", "EMAIL", "CAMPAIGN"));
        List<String> mkto = new ArrayList<String>(Arrays.asList("", "", ""));
        mappings.schemaColumnName.setValue(schema);
        mappings.marketoColumnName.setValue(mkto);

        assertEquals(schema, Arrays.asList(mappings.getNameMappingsForMarketo().keySet().toArray()));
        assertEquals(schema, Arrays.asList(mappings.getNameMappingsForMarketo().values().toArray()));
    }

    @Test
    public void testGetNameMappingsForMarketoFullMappings() throws Exception {
        List<String> schema = new ArrayList<String>(Arrays.asList("ID", "EMAIL", "CAMPAIGN"));
        List<String> mkto = new ArrayList<String>(Arrays.asList("LeadId", "Email", "Campaign"));
        mappings.schemaColumnName.setValue(schema);
        mappings.marketoColumnName.setValue(mkto);

        assertEquals(schema, Arrays.asList(mappings.getNameMappingsForMarketo().keySet().toArray()));
        assertEquals(mkto, Arrays.asList(mappings.getNameMappingsForMarketo().values().toArray()));
    }

    @Test
    public void testGetNameMappingsForMarketoSemiMappings() throws Exception {
        List<String> schema = new ArrayList<String>(Arrays.asList("ID", "EMAIL", "CAMPAIGN"));
        List<String> mkto = new ArrayList<String>(Arrays.asList("LeadId", "", "Campaign"));
        mappings.schemaColumnName.setValue(schema);
        mappings.marketoColumnName.setValue(mkto);

        assertEquals(schema, Arrays.asList(mappings.getNameMappingsForMarketo().keySet().toArray()));
        assertEquals(Arrays.asList("LeadId", "EMAIL", "Campaign"),
                Arrays.asList(mappings.getNameMappingsForMarketo().values().toArray()));
    }

    @Test
    public void testGetNameMappingsForMarketoSemiMappingsWithNull() throws Exception {
        List<String> schema = new ArrayList<String>(Arrays.asList("ID", "EMAIL", "CAMPAIGN"));
        List<String> mkto = new ArrayList<String>(Arrays.asList("LeadId", null, ""));
        mappings.schemaColumnName.setValue(schema);
        mappings.marketoColumnName.setValue(mkto);
        assertEquals(schema, Arrays.asList(mappings.getNameMappingsForMarketo().keySet().toArray()));
        assertEquals(Arrays.asList("LeadId", "EMAIL", "CAMPAIGN"),
                Arrays.asList(mappings.getNameMappingsForMarketo().values().toArray()));
    }
}
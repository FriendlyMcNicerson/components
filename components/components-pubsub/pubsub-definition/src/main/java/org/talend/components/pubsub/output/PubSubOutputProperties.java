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

package org.talend.components.pubsub.output;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.common.io.IOProperties;
import org.talend.components.pubsub.PubSubDatasetDefinition;
import org.talend.components.pubsub.PubSubDatasetProperties;
import org.talend.daikon.properties.ReferenceProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PubSubOutputProperties extends FixedConnectorsComponentProperties implements IOProperties {

    public ReferenceProperties<PubSubDatasetProperties> datasetRef = new ReferenceProperties<>("datasetRef",
            PubSubDatasetDefinition.NAME);

    protected transient PropertyPathConnector MAIN_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "dataset.main");

    public PubSubOutputProperties(String name) {
        super(name);
    }

    public Property<String> idLabel = PropertyFactory.newString("idLabel");

    public Property<String> timestampLabel = PropertyFactory.newString("timestampLabel");

    @Override
    public void setupLayout() {
         super.setupLayout();
         Form mainForm = new Form(this, Form.MAIN);
         mainForm.addRow(idLabel);
         mainForm.addRow(timestampLabel);
    }

    @Override
    public PubSubDatasetProperties getDatasetProperties() {
        return datasetRef.getReference();
    }

    @Override
    public void setDatasetProperties(DatasetProperties datasetProperties) {
        datasetRef.setReference(datasetProperties);
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        HashSet<PropertyPathConnector> connectors = new HashSet<>();
        if (isOutputConnection) {
            return Collections.EMPTY_SET;
        } else {
            connectors.add(MAIN_CONNECTOR);
        }
        return connectors;
    }
}

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
package org.talend.components.salesforce.tsalesforcegetservertimestamp;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentConnector;
import org.talend.components.api.component.ComponentConnector.Type;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.ComponentRuntime;
import org.talend.components.api.schema.Schema;
import org.talend.components.salesforce.SalesforceDefinition;
import org.talend.components.salesforce.SalesforceRuntime;
import org.talend.components.salesforce.tsalesforcegetdeleted.TSalesforceGetDeletedProperties;

@org.springframework.stereotype.Component(Constants.COMPONENT_BEAN_PREFIX
        + TSalesforceGetServerTimestampDefinition.COMPONENT_NAME)
@aQute.bnd.annotation.component.Component(name = Constants.COMPONENT_BEAN_PREFIX
        + TSalesforceGetServerTimestampDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TSalesforceGetServerTimestampDefinition extends SalesforceDefinition {

    public static final String COMPONENT_NAME = "tSalesforceGetServerTimestamp"; //$NON-NLS-1$

    public TSalesforceGetServerTimestampDefinition() {
        super(COMPONENT_NAME);
        propertiesClass = TSalesforceGetServerTimestampProperties.class;
        setConnectors(new ComponentConnector(Type.FLOW, 0, 0), new ComponentConnector(Type.ITERATE, 1, 0),
                new ComponentConnector(Type.SUBJOB_OK, 1, 0), new ComponentConnector(Type.SUBJOB_ERROR, 1, 0));
    }

    @Override
    public ComponentRuntime createRuntime() {
        return new SalesforceRuntime() {

            @Override
            public void inputBegin(ComponentProperties props) throws Exception {

                TSalesforceGetDeletedProperties gdProps = (TSalesforceGetDeletedProperties) props;
                Schema column = (Schema) gdProps.module.schema.getValue(gdProps.module.schema.schema);

                Calendar result = getServerTimestamp();
                Map<String, Object> map = new HashMap<>();
                // FIXME - error checking - what if there are no columns
                map.put(column.getRoot().getChildren().get(0).getName(), result);
            }

        };
    }

    public boolean isStartable() {
        return true;
    }

    public String getPartitioning() {
        return AUTO;
    }

}

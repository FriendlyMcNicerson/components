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
package org.talend.components.salesforce.tsalesforceconnect;

import org.springframework.stereotype.Component;
import org.talend.components.api.ComponentConnector;
import org.talend.components.api.ComponentConnector.Type;
import org.talend.components.api.ComponentDefinition;
import org.talend.components.api.ComponentProperties;
import org.talend.components.api.Constants;
import org.talend.components.salesforce.SalesforceConnectionProperties;

@org.springframework.stereotype.Component(Constants.COMPONENT_BEAN_PREFIX + TSalesforceConnectDefinition.COMPONENT_NAME)
@aQute.bnd.annotation.component.Component(name = Constants.COMPONENT_BEAN_PREFIX + TSalesforceConnectDefinition.COMPONENT_NAME)
public class TSalesforceConnectDefinition implements ComponentDefinition {

    public static final String               COMPONENT_NAME = "tSalesforceConnect";                     //$NON-NLS-1$

    protected ComponentConnector[]           connectors     = { new ComponentConnector(Type.FLOW, 0, 0),
            new ComponentConnector(Type.ITERATE, 1, 0), new ComponentConnector(Type.SUBJOB_OK, 1, 0),
            new ComponentConnector(Type.SUBJOB_ERROR, 1, 0) };

    public ComponentProperties createProperties() {
        return new TSalesforceConnectProperties();
    }

    public Family[] getSupportedFamilies() {
        return new Family[] { Family.BUSINESS, Family.CLOUD };
    }

    public String getName() {
        return COMPONENT_NAME;
    }

}

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
package org.talend.components.salesforce.tsalesforceoutput;

import org.talend.components.api.Constants;
import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ComponentConnector;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.ComponentConnector.Type;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.ComponentRuntime;
import org.talend.components.salesforce.SalesforceRuntime;

/**
 * Component that can connect to a salesforce system and put some data into it.
 */

@org.springframework.stereotype.Component(Constants.COMPONENT_BEAN_PREFIX + TSalesforceOutputDefinition.COMPONENT_NAME)
@aQute.bnd.annotation.component.Component(name = Constants.COMPONENT_BEAN_PREFIX
        + TSalesforceOutputDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TSalesforceOutputDefinition extends AbstractComponentDefinition {

    public static final String COMPONENT_NAME = "tSalesforceOutput"; //$NON-NLS-1$

    public TSalesforceOutputDefinition() {
        setConnectors(new ComponentConnector(Type.FLOW, 1, 0), new ComponentConnector(Type.MAIN, 0, 1),
                new ComponentConnector(Type.REJECT, 0, 1), new ComponentConnector(Type.SUBJOB_OK, 1, 0),
                new ComponentConnector(Type.SUBJOB_ERROR, 1, 0));
    }

    @Override
    public ComponentProperties createProperties() {
        return new TSalesforceOutputProperties(null);
    }

    @Override
    public ComponentRuntime createRuntime() {
        return new SalesforceRuntime();
    }

    @Override
    public String[] getSupportedFamilies() {
        return new String[] { ComponentDefinition.FAMILY_BUSINESS, ComponentDefinition.FAMILY_CLOUD };
    }

    @Override
    public String getName() {
        return COMPONENT_NAME;
    }

    @Override
    public String getPngImagePath(ComponentImageType imageType) {
        switch (imageType) {
        case PALLETE_ICON_32X32:
            return "tSalesforceOutput_icon32.png"; //$NON-NLS-1$

        default:
            // will return null
        }
        return null;
    }

    @Override
    public String getDisplayName() {
        return "tSalesforceOutput";
    }

}

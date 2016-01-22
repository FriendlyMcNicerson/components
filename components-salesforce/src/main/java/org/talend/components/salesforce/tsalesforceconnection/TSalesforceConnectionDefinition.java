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
package org.talend.components.salesforce.tsalesforceconnection;

import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentConnector;
import org.talend.components.api.component.ComponentConnector.Type;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.ValidationResult;
import org.talend.components.api.runtime.ComponentRuntime;
import org.talend.components.salesforce.SalesforceConnectionProperties;
import org.talend.components.salesforce.SalesforceDefinition;
import org.talend.components.salesforce.SalesforceRuntime;

import aQute.bnd.annotation.component.Component;

@Component(name = Constants.COMPONENT_BEAN_PREFIX
        + TSalesforceConnectionDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TSalesforceConnectionDefinition extends SalesforceDefinition {

    public static final String COMPONENT_NAME = "tSalesforceConnectionNew"; //$NON-NLS-1$

    public TSalesforceConnectionDefinition() {
        super(COMPONENT_NAME);
        setConnectors(new ComponentConnector(Type.FLOW, 0, 0), new ComponentConnector(Type.ITERATE, 1, 0),
                new ComponentConnector(Type.SUBJOB_OK, 1, 0), new ComponentConnector(Type.SUBJOB_ERROR, 1, 0));
    }

    @Override
    public ComponentRuntime createRuntime() {
        return new SalesforceRuntime() {
            //
            // FIXME - change me
            // Need to adjust the override after we have a finally solution to split Runtime class
            //
            @Override
            public void inputBegin(ComponentProperties props) throws Exception {
            	SalesforceConnectionProperties properties = (SalesforceConnectionProperties) props;
            	ValidationResult result = connectWithResult(properties);
            	if(ValidationResult.Result.ERROR.equals(result.getStatus())){
            		throw new Exception(result.getMessage());
            	}
            }

        };
    }

    @Override
    public boolean isStartable() {
        return true;
    }

    @Override
    public Class<?> getPropertyClass() {
        return SalesforceConnectionProperties.class;
    }

}

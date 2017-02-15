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
package org.talend.components.marketo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.runtime.RuntimeInfo;

public abstract class MarketoComponentDefinition extends AbstractComponentDefinition {

    public static final String RETURN_NB_CALL = "numberCall";

    public static final Property<Integer> RETURN_NB_CALL_PROP = PropertyFactory.newInteger(RETURN_NB_CALL);

    private transient static final Logger LOG = LoggerFactory.getLogger(MarketoComponentDefinition.class);

    public MarketoComponentDefinition(String componentName) {
        super(componentName, ExecutionEngine.DI);
        setupI18N(new Property<?>[]{RETURN_NB_CALL_PROP});
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Property[] getReturnProperties() {
        return new Property[]{RETURN_ERROR_MESSAGE_PROP, RETURN_NB_CALL_PROP};
    }

    @Override
    public boolean isStartable() {
        return true;
    }

    @Override
    public boolean isSchemaAutoPropagate() {
        return true;
    }

    @Override
    public String[] getFamilies() {
        return new String[]{"Business/Marketo", "Cloud/Marketo"};
    }

    public RuntimeInfo getRuntimeInfo(String runtimeClassName) {
        return new MarketoRuntimeInfo(runtimeClassName);
    }

    @Override
    public Class<? extends ComponentProperties>[] getNestedCompatibleComponentPropertiesClass() {
        return new Class[]{TMarketoConnectionProperties.class};
    }
}

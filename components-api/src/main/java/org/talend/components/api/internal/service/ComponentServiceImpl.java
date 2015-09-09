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
package org.talend.components.api.internal.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.talend.components.api.*;

/**
 * Main Component Service implementation that is not related to any framework (neither OSGI, nor Spring) it uses a
 * ComponentRegistry implementation that will be provided by framework specific Service classes
 */
public class ComponentServiceImpl implements ComponentService {

    private ComponentRegistry componentRegistry;

    public ComponentServiceImpl(ComponentRegistry componentRegistry) {
        this.componentRegistry = componentRegistry;
    }

    @Override
    public Set<String> getAllComponentNames() {
        // remove the components# internal prefix to return the simple name
        Collection<String> componentsInternalNames = componentRegistry.getComponents().keySet();
        Set<String> compNames = new HashSet<>(componentsInternalNames.size());
        for (String name : componentsInternalNames) {
            compNames.add(name.substring(Constants.COMPONENT_BEAN_PREFIX.length()));
        }
        return compNames;
    }

    @Override
    public Set<ComponentDefinition> getAllComponents() {
        return new HashSet<>(componentRegistry.getComponents().values());
    }

    @Override
    public Set<ComponentWizardDefinition> getTopLevelComponentWizards() {
        return new HashSet<>(componentRegistry.getComponentWizards().values());
    }

    @Override
    public ComponentProperties getComponentProperties(String name) {
        final String beanName = Constants.COMPONENT_BEAN_PREFIX + name;
        ComponentDefinition compDef = componentRegistry.getComponents().get(beanName);
        ComponentProperties properties = compDef.createProperties();
        return properties;
    }

    @Override
    public ComponentProperties validateProperty(String propName, ComponentProperties properties) throws Throwable {
        properties.validateProperty(propName);
        return properties;
    }

    @Override
    public ComponentProperties beforeProperty(String propName, ComponentProperties properties) throws Throwable {
        properties.beforeProperty(propName);
        return properties;
    }

    @Override
    public ComponentProperties afterProperty(String propName, ComponentProperties properties) throws Throwable {
        properties.afterProperty(propName);
        return properties;
    }

}

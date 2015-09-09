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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.ComponentDefinition;
import org.talend.components.api.ComponentProperties;
import org.talend.components.api.ComponentService;
import org.talend.components.api.ComponentWizardDefinition;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

/**
 * This is the OSGI specific service implementation that completely delegates the implementation to the Framework
 * agnostic {@link ComponentServiceImpl}
 */
@Component
public class ComponentServiceOsgi implements ComponentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentServiceOsgi.class);

    /**
     * created by sgandon on 7 sept. 2015 Detailled comment
     */
    private final class ComponentRegistryOsgi implements ComponentRegistry {

        private BundleContext bc;

        public ComponentRegistryOsgi(BundleContext bc) {
            this.bc = bc;

        }

        private Map<String, ComponentDefinition>       components;

        private Map<String, ComponentWizardDefinition> componentWizards;

        protected Map populateMap(Class cls) {
            Map map = new HashMap<String, Object>();
            try {
                Collection<ServiceReference<ComponentDefinition>> serviceReferences = bc.getServiceReferences(cls, null);
                for (ServiceReference sr : serviceReferences) {
                    Object service = bc.getService(sr);
                    Object nameProp = sr.getProperty("component.name"); //$NON-NLS-1$
                    if (nameProp instanceof String) {
                        map.put((String) nameProp, service);
                        LOGGER.info("Registered the component: " + nameProp + "(" + service.getClass().getCanonicalName() + ")");
                    } else {// no name set so issue a warning
                        LOGGER.warn("Failed to register the following component because it is unnamed: "
                                + service.getClass().getCanonicalName());
                    }
                    return map;
                }
                if (components.isEmpty()) {// warn if not comonents where registered
                    LOGGER.warn("Could not find any registered components.");
                } // else everything is fine
            } catch (InvalidSyntaxException e) {
                LOGGER.error("Failed to get ComponentDefinition services", e); //$NON-NLS-1$
            }
            return map;
        }

        @Override
        public Map<String, ComponentDefinition> getComponents() {
            if (components == null)
                components = populateMap(ComponentDefinition.class);
            return components;
        }

        @Override
        public Map<String, ComponentWizardDefinition> getComponentWizards() {
            if (componentWizards == null)
                componentWizards = populateMap(ComponentWizardDefinition.class);
            return componentWizards;
        }
    }

    private ComponentService componentServiceDelegate;

    @Activate
    void activate(BundleContext bundleContext) throws InvalidSyntaxException {
        this.componentServiceDelegate = new ComponentServiceImpl(new ComponentRegistryOsgi(bundleContext));
    }

    @Override
    public ComponentProperties getComponentProperties(String name) {
        return componentServiceDelegate.getComponentProperties(name);
    }

    @Override
    public ComponentProperties validateProperty(String propName, ComponentProperties properties) throws Throwable {
        return componentServiceDelegate.validateProperty(propName, properties);
    }

    @Override
    public ComponentProperties beforeProperty(String propName, ComponentProperties properties) throws Throwable {
        return componentServiceDelegate.beforeProperty(propName, properties);
    }

    @Override
    public ComponentProperties afterProperty(String propName, ComponentProperties properties) throws Throwable {
        return componentServiceDelegate.afterProperty(propName, properties);
    }

    @Override
    public Set<String> getAllComponentNames() {
        return componentServiceDelegate.getAllComponentNames();
    }

    @Override
    public Set<ComponentDefinition> getAllComponents() {
        return componentServiceDelegate.getAllComponents();
    }

    @Override public Set<ComponentWizardDefinition> getTopLevelComponentWizards() {
        return componentServiceDelegate.getTopLevelComponentWizards();
    }

}

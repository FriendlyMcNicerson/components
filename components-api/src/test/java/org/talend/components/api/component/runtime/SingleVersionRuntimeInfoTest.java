// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.api.component.runtime;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ops4j.pax.url.mvn.Handler;
import org.ops4j.pax.url.mvn.ServiceConstants;
import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.property.Property;

public class SingleVersionRuntimeInfoTest {

    @BeforeClass
    public static void setupMavenUrlHandler() {
        URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {

            @Override
            public URLStreamHandler createURLStreamHandler(String protocol) {
                if (ServiceConstants.PROTOCOL.equals(protocol)) {
                    return new Handler();
                } else {
                    return null;
                }
            }
        });

    }

    /**
     * Test method for {@link org.talend.components.api.component.runtime.SimpleRuntimeInfo#getMavenUrlDependencies()}.
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testGetMavenUriDependencies() throws MalformedURLException {
        AbstractComponentDefinition cd = new AbstractComponentDefinition("") {

            @Override
            public Property[] getReturnProperties() {
                return null;
            }

            @Override
            public Class<? extends ComponentProperties> getPropertyClass() {
                return null;
            }

            @Override
            public RuntimeInfo getRuntimeInfo(Properties properties, ConnectorTopology componentType) {
                return null;
            }

            @Override
            public Set<ConnectorTopology> getSupportedConnectorTopologies() {
                return null;
            }
        };
        SimpleRuntimeInfo runtimeInfo = new SimpleRuntimeInfo(cd.getClass().getClassLoader(), "org.talend.components.api.test",
                "test-components", null);
        List<URL> mavenUriDependencies = runtimeInfo.getMavenUrlDependencies();
        assertEquals(5, mavenUriDependencies.size());
        assertThat(mavenUriDependencies,
                containsInAnyOrder(new URL("mvn:org.apache.maven/maven-core/3.3.3/jar"), //
                        new URL("mvn:org.eclipse.sisu/org.eclipse.sisu.plexus/0.0.0.M2a/jar"), //
                        new URL("mvn:org.apache.maven/maven-artifact/3.3.3/jar"), //
                        new URL("mvn:org.eclipse.aether/aether-transport-file/1.0.0.v20140518/jar"), //
                        new URL("mvn:org.talend.components/file-input/0.1.0.SNAPSHOT/jar")));

    }

    /**
     * Test method for {@link org.talend.components.api.component.runtime.SimpleRuntimeInfo#getRuntimeClassName()}.
     */
    @Test
    public void testGetRuntimeClassName() {
        SimpleRuntimeInfo runtimeInfo = new SimpleRuntimeInfo(null, null, null, "org.talend.mr.Robot");
        assertEquals("org.talend.mr.Robot", runtimeInfo.getRuntimeClassName());
    }

}

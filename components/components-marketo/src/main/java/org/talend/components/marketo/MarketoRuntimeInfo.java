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

import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.JarRuntimeInfo;

public class MarketoRuntimeInfo extends JarRuntimeInfo {

    public static final String MAVEN_DEFINITION_ARTIFACT_ID = "marketo-definition";

    public static final String MAVEN_GROUP_ID = "org.talend.components";

    public static final String MAVEN_RUNTIME_ARTIFACT_ID = "marketo-runtime";

    public static final String MAVEN_RUNTIME_PATH = "mvn:org.talend.components/marketo-runtime";

    public static final String RUNTIME_SINK_CLASS = "org.talend.components.marketo.runtime.MarketoSink";

    public static final String RUNTIME_SOURCEORSINK_CLASS = "org.talend.components.marketo.runtime.MarketoSourceOrSink";

    public static final String RUNTIME_SOURCE_CLASS = "org.talend.components.marketo.runtime.MarketoSource";

    public MarketoRuntimeInfo(String runtimeClassName) {
        super(MAVEN_RUNTIME_PATH, DependenciesReader.computeDependenciesFilePath(MAVEN_GROUP_ID, MAVEN_RUNTIME_ARTIFACT_ID),
                runtimeClassName);
    }
}

package org.talend.components.api.runtime.spark;

import java.util.List;
import java.util.Map;

import org.talend.components.api.facet.SimpleTransformationFacet;
import org.talend.components.api.runtime.ReturnObject;
import org.talend.components.api.runtime.SimpleTransformationRuntime;

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

/**
 * created by pbailly on 18 Dec 2015 Detailled comment
 *
 */
public class DISimpleTransformationRuntime implements SimpleTransformationRuntime<List<Map<String, Object>>> {

    ReturnObject returnObject = new ReturnObject();

    SimpleTransformationFacet facet;

    public DISimpleTransformationRuntime(SimpleTransformationFacet facet) {
        this.facet = facet;
    }

    @Override
    public void genericEexcute(List<Map<String, Object>> inputs) throws Exception {
        for (Map<String, Object> input : inputs) {
            facet.execute(input, returnObject);
        }
    }

    @Override
    public List<Map<String, Object>> getMainOutput() {
        return returnObject.getMainOutput();
    }

}

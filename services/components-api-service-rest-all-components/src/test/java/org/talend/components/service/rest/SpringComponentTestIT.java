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
package org.talend.components.service.rest;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.test.AbstractComponentTest;
import org.talend.daikon.definition.service.DefinitionRegistryService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SpringComponentTestIT extends AbstractComponentTest {

    @Inject
    private ComponentService componentService;

    @Override
    public ComponentService getComponentService() {
        return componentService;
    }

    @Inject
    DefinitionRegistryService osgiDefinitionService;

    @Override
    public DefinitionRegistryService getDefinitionRegistry() {
        return osgiDefinitionService;
    }

}

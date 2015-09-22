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
package org.talend.components.api.wizard;

import org.talend.components.api.AbstractTopLevelDefinition;

/**
 * Helper to create a ComponentWizardDefinition with I18N handling
 */
public abstract class AbstractComponentWizardDefintion extends AbstractTopLevelDefinition implements ComponentWizardDefinition {

    /**
     * 
     */
    private static final String I18N_MENU_NAME_SUFFIX = ".menu.name"; //$NON-NLS-1$

    @Override
    protected String getI18nPrefix() {
        return "wizard."; //$NON-NLS-1$
    }

    @Override
    public String getMenuItemName() {
        return getI18nMessage(getI18nPrefix() + getName() + I18N_MENU_NAME_SUFFIX);
    }
}

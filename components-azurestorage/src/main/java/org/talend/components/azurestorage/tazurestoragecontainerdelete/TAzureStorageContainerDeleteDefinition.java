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
package org.talend.components.azurestorage.tazurestoragecontainerdelete;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.azurestorage.AzureStorageContainerDefinition;

public class TAzureStorageContainerDeleteDefinition extends AzureStorageContainerDefinition {

    public static final String COMPONENT_NAME = "tAzureStorageContainerDelete"; //$NON-NLS-1$

    public TAzureStorageContainerDeleteDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TAzureStorageContainerDeleteProperties.class;
    }

    @Override
    public Class getPropertiesClass() {
        return TAzureStorageContainerDeleteProperties.class;
    }

}

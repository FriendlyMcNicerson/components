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
package org.talend.components.azurestorage.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.azurestorage.AzureStorageBaseTestIT;
import org.talend.components.azurestorage.tazurestoragecontainercreate.TAzureStorageContainerCreateProperties;
import org.talend.components.azurestorage.tazurestoragecontainercreate.TAzureStorageContainerCreateProperties.AccessControl;

import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;

public class AzureStorageContainerCreateReaderTestIT extends AzureStorageBaseTestIT {

    BoundedReader reader;

    public AzureStorageContainerCreateReaderTestIT() {
        super("container-create-" + getRandomTestUID());
        // TEST_NAME = ;
    }

    @Before
    public void cleanupTestContainers() throws Exception {
        for (String c : TEST_CONTAINERS) {
            doContainerDelete(getNamedThingForTest(c));
        }
    }

    @Override
    public Boolean doContainerCreate(String container, AccessControl access) throws Exception {
        Boolean result;
        TAzureStorageContainerCreateProperties properties = new TAzureStorageContainerCreateProperties("tests");
        ((TAzureStorageContainerCreateProperties) properties).setupProperties();
        properties.container.setValue(container);
        properties.accessControl.setValue(access);
        setupContainerProperties(properties);
        reader = createBoundedReader(properties);
        result = reader.start();
        assertFalse(reader.advance());
        Object row = reader.getCurrent();
        assertNotNull(row);

        BlobContainerPublicAccessType cAccess = ((AzureStorageSource) reader.getCurrentSource())
                .getStorageContainerReference(runtime, container).downloadPermissions().getPublicAccess();
        if (access == AccessControl.Public)
            assertEquals(BlobContainerPublicAccessType.CONTAINER, cAccess);
        else
            assertEquals(BlobContainerPublicAccessType.OFF, cAccess);

        return result;
    }

    @Test
    public void testCreateContainer() throws Exception {
        assertTrue(doContainerCreate(getNamedThingForTest(TEST_CONTAINER_1), AccessControl.Private));
        assertFalse(doContainerCreate(getNamedThingForTest(TEST_CONTAINER_1), AccessControl.Private));
        assertTrue(doContainerCreate(getNamedThingForTest(TEST_CONTAINER_2), AccessControl.Private));
        assertTrue(doContainerCreate(getNamedThingForTest(TEST_CONTAINER_3), AccessControl.Public));
    }
}

package org.talend.components.api;

import org.talend.components.api.schema.ComponentSchemaElement;
import org.talend.components.api.schema.internal.ComponentSchemaElementImpl;

/**
 *
 */
public abstract class ComponentRuntime {

    public ComponentSchemaElement getComponentSchemaElement() {
        return new ComponentSchemaElementImpl();
    }

}

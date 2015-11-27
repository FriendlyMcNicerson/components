package org.talend.components.api.schema;

import org.talend.components.api.schema.internal.DataSchemaElement;
import org.talend.components.api.schema.internal.SchemaImpl;

import com.cedarsoftware.util.io.JsonReader;

/**
 * Make objects that are related to the schemas.
 */
public class SchemaFactory {

    public static Schema newSchema() {
        return new SchemaImpl();
    }

    public static SchemaElement newSchemaElement(SchemaElement.Type type, String name) {
        SchemaElement se = newSchemaElement(name);
        se.setType(type);
        return se;
    }

    public static SchemaElement newSchemaElement(String name) {
        SchemaElement se = new DataSchemaElement();
        se.setName(name);
        return se;
    }

    /**
     * Returns a {@link Schema} object materialized from the serialized string. See {@link Schema#toSerialized()}.
     */
    public static Schema fromSerialized(String serialized) {
        Schema deser = null;
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(SchemaFactory.class.getClassLoader());
            deser = (Schema) JsonReader.jsonToJava(serialized);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
        return deser;

    }

}

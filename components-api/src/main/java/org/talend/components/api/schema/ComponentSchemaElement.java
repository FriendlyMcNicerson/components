package org.talend.components.api.schema;

import java.util.List;

/**
 *
 */
public interface ComponentSchemaElement {

    public enum Type {
        STRING,
        INT,
        DATE,
        DYNAMIC
    }

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public Type getType();

    public void setType(Type type);

    public int getSize();

    public void setSize(int size);

    public List<ComponentSchemaElement> getChildren();

    public void setChildren(List<ComponentSchemaElement> children);

    public void addChild(ComponentSchemaElement child);

}

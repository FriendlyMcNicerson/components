package org.talend.components.netsuite;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.avro.Schema;
import org.talend.components.netsuite.client.NetSuiteMetaData;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;

/**
 *
 */
public class NetSuiteSchemaManager {

    private static final NetSuiteSchemaManager instance = new NetSuiteSchemaManager();

    public static NetSuiteSchemaManager getInstance() {
        return instance;
    }

    /**
     * Infers an Avro schema for the given NsObject. This can be an expensive operation so the schema
     * should be cached where possible. This is always an {@link Schema.Type#RECORD}.
     *
     * @param in the <code>NsObject</code> to analyse.
     * @return the schema for data given from the object.
     */
    public Schema inferSchemaForEntity(NetSuiteMetaData.EntityInfo in) {
        List<Schema.Field> fields = new ArrayList<>();

        for (NetSuiteMetaData.FieldInfo fieldInfo : in.getFields()) {

            Schema.Field avroField = new Schema.Field(fieldInfo.getName(),
                    inferSchemaForField(fieldInfo), null, (Object) null);
            // Add some Talend6 custom properties to the schema.
            Schema avroFieldSchema = avroField.schema();
            if (avroFieldSchema.getType() == Schema.Type.UNION) {
                for (Schema schema : avroFieldSchema.getTypes()) {
                    if (avroFieldSchema.getType() != Schema.Type.NULL) {
                        avroFieldSchema = schema;
                        break;
                    }
                }
            }

            if (AvroUtils.isSameType(avroFieldSchema, AvroUtils._string())) {
                if (fieldInfo.getLength() != 0) {
                    avroField.addProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH, String.valueOf(fieldInfo.getLength()));
                }
            }

            Class<?> fieldType = fieldInfo.getValueType();
            if (fieldType == XMLGregorianCalendar.class) {
                avroField.addProp(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'.000Z'");
            }

            if (avroField.defaultVal() != null) {
                avroField.addProp(SchemaConstants.TALEND_COLUMN_DEFAULT, String.valueOf(avroField.defaultVal()));
            }

            fields.add(avroField);
        }

        return Schema.createRecord(in.getName(), null, null, false, fields);
    }

    /**
     * Infers an Avro schema for the given FieldInfo. This can be an expensive operation so the schema should be
     * cached where possible. The return type will be the Avro Schema that can contain the fieldInfo data without loss of
     * precision.
     *
     * @param fieldInfo the FieldInfo to analyse.
     * @return the schema for data that the fieldInfo describes.
     */
    public Schema inferSchemaForField(NetSuiteMetaData.FieldInfo fieldInfo) {
        Schema base;

        Class<?> fieldType = fieldInfo.getValueType();

        if (fieldType == Boolean.TYPE || fieldType == Boolean.class) {
            base = AvroUtils._boolean();
        } else if (fieldType == Integer.TYPE || fieldType == Integer.class) {
            base = AvroUtils._int();
        } else if (fieldType == Long.TYPE || fieldType == Long.class) {
            base = AvroUtils._long();
        } else if (fieldType == Float.TYPE || fieldType == Float.class) {
            base = AvroUtils._float();
        } else if (fieldType == Double.TYPE || fieldType == Double.class) {
            base = AvroUtils._double();
        } else if (fieldType == XMLGregorianCalendar.class) {
            base = AvroUtils._string();
        } else if (fieldType == String.class) {
            base = AvroUtils._string();
        } else if (fieldType.isEnum()) {
            base = AvroUtils._string();
        } else {
            base = AvroUtils._string();
        }

        base = fieldInfo.isNullable() ? AvroUtils.wrapAsNullable(base) : base;

        return base;
    }

}

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
package org.talend.components.marketo.runtime.client.rest.type;

import static org.apache.avro.SchemaBuilder.record;

import java.util.Arrays;
import java.util.Date;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.avro.SchemaConstants;

import com.google.gson.Gson;

public class CustomObject {

    private transient static final Logger LOG = LoggerFactory.getLogger(CustomObject.class);

    String name;

    String displayName;

    String description;

    Date createdAt;

    Date updatedAt;

    String idField;

    String[] dedupeFields;

    String[][] searchableFields;

    ObjectField[] fields;

    ObjectRelation[] relationships;

    public class ObjectField {

        String dataType;

        String displayName;

        Integer length;

        String name;

        Boolean updateable;

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getUpdateable() {
            return updateable;
        }

        public void setUpdateable(Boolean updateable) {
            this.updateable = updateable;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ObjectField{");
            sb.append("name='").append(name).append('\'');
            sb.append(", displayName='").append(displayName).append('\'');
            sb.append(", dataType='").append(dataType).append('\'');
            sb.append(", length=").append(length);
            sb.append(", updateable=").append(updateable);
            sb.append('}');
            return sb.toString();
        }
    }

    public class ObjectRelation {

        String field;

        RelatedObject relatedTo;// relatedTo (RelatedObject, optional): Object to which the field is linked ,

        String type;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public RelatedObject getRelatedTo() {
            return relatedTo;
        }

        public void setRelatedTo(RelatedObject relatedTo) {
            this.relatedTo = relatedTo;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ObjectRelation{");
            sb.append("field='").append(field).append('\'');
            sb.append(", relatedTo=").append(relatedTo);
            sb.append(", type='").append(type).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public class RelatedObject {

        String name;

        String field;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getField() {

            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("RelatedObject{");
            sb.append("name='").append(name).append('\'');
            sb.append(", field='").append(field).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public String[] getDedupeFields() {
        return dedupeFields;
    }

    public void setDedupeFields(String[] dedupeFields) {
        this.dedupeFields = dedupeFields;
    }

    public String[][] getSearchableFields() {
        return searchableFields;
    }

    public void setSearchableFields(String[][] searchableFields) {
        this.searchableFields = searchableFields;
    }

    public ObjectField[] getFields() {
        return fields;
    }

    public void setFields(ObjectField[] fields) {
        this.fields = fields;
    }

    public ObjectRelation[] getRelationships() {
        return relationships;
    }

    public void setRelationships(ObjectRelation[] relationships) {
        this.relationships = relationships;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CustomObject{");
        sb.append("name='").append(name).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", idField='").append(idField).append('\'');
        sb.append(", dedupeFields=").append(dedupeFields == null ? "null" : Arrays.asList(dedupeFields).toString());
        sb.append(", searchableFields=").append(searchableFields == null ? "null" : Arrays.deepToString(searchableFields));
        sb.append(", fields=").append(fields == null ? "null" : Arrays.asList(fields).toString());
        sb.append(", relationships=").append(relationships == null ? "null" : Arrays.asList(relationships).toString());
        sb.append('}');
        return sb.toString();
    }

    /**
     * {"requestId":"1495b#15a46590a99","result":[{"name":"smartphone_c","displayName":"Smartphone","description":"Smartphone
     * possessed by lead","createdAt":"2017-02-15T16:06:35Z","updatedAt":"2017-02-15T16:06:36Z",
     * "idField":"marketoGUID","dedupeFields":["model"],"searchableFields":[["model"],["marketoGUID"],["customerId"]],
     * "relationships":[{"field":"customerId","type":"child","relatedTo":{"name":"Lead","field":"Id"}}],"fields":[{"name":"createdAt","displayName":"Created
     * At","dataType":"datetime","updateable":false},{"name":"marketoGUID","displayName":"Marketo
     * GUID","dataType":"string","length":36,"updateable":false},{"name":"updatedAt","displayName":"Updated
     * At","dataType":"datetime","updateable":false},{"name":"acquiredAt","displayName":"Acquired
     * at","dataType":"date","updateable":true},{"name":"brand","displayName":"Brand","dataType":"string","length":255,"updateable":true},{"name":"customerId","displayName":"Customer
     * Id","dataType":"integer","updateable":true},{"name":"model","displayName":"Model","dataType":"string","length":255,"updateable":true}]}],"success":true}
     * 
     */

    public static Schema getSchema() {
        return record("CustomObject").fields()//
                .name("name").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().stringType().noDefault()//
                .name("displayName").type().nullable().stringType().noDefault()//
                .name("description").type().nullable().stringType().noDefault()//
                .name("createdAt").prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-ddTHH:mm:ssZ").type().nullable()
                .longType().noDefault()//
                .name("updatedAt").prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-ddTHH:mm:ssZ").type().nullable()
                .longType().noDefault()// .type.Date//
                .name("idField").type().nullable().stringType().noDefault()//
                .name("dedupeFields").type().nullable().stringType().noDefault()// String []
                .name("searchableFields").type().nullable().stringType().noDefault()// String [] []
                .name("fields").type().nullable().stringType().noDefault()// ObjectField[]
                .name("relationships").type().nullable().stringType().noDefault()// ObjectRelation[]
                //
                .endRecord();
    }

    public IndexedRecord toIndexedRecord() {
        Gson gson = new Gson();
        IndexedRecord record = new GenericData.Record(getSchema());
        record.put(0, getName());
        record.put(1, getDisplayName());
        record.put(2, getDescription());
        record.put(3, getCreatedAt());
        record.put(4, getUpdatedAt());
        record.put(5, getIdField());
        record.put(6, gson.toJson(getDedupeFields()));
        record.put(7, gson.toJson(getSearchableFields()));
        record.put(8, gson.toJson(getFields()));
        record.put(9, gson.toJson(getRelationships()));

        return record;
    }
}

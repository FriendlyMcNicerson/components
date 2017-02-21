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
package org.talend.components.marketo;

import static org.apache.avro.SchemaBuilder.record;

import java.util.Date;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;

/**
 * Common shared schemas and constants
 * 
 */
public class MarketoConstants {

    public static final String API_REST = "REST";

    public static final String API_SOAP = "SOAP";

    public static final String FIELD_ERROR_MSG = "ERROR_MSG";

    public static final String FIELD_STATUS = "Status";

    public static final String FIELD_SUCCESS = "Success";

    public static final String FIELD_LEAD_ID = "LeadId";

    public static final String FIELD_LIST_ID = "ListId";

    public static final String FIELD_LEAD_KEY_VALUE = "LeadKeyValue";

    public static final String FIELD_LEAD_KEY_TYPE = "LeadKeyType";

    public static final String FIELD_LIST_KEY_VALUE = "ListKeyValue";

    public static final String FIELD_LIST_KEY_TYPE = "ListKeyType";

    /**
     * List Operations
     */
    /////////////////////////////////////////////////////////////////////////
    // REST
    /////////////////////////////////////////////////////////////////////////
    public static Schema getListOperationRESTSchema() {
        return SchemaBuilder.builder().record("REST").fields() //
                .name(FIELD_LIST_ID)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().intType().noDefault() //
                //
                .name(FIELD_LEAD_ID)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().intType().noDefault() //
                .endRecord();
    }

    /////////////////////////////////////////////////////////////////////////
    // SOAP
    /////////////////////////////////////////////////////////////////////////
    public static Schema getListOperationSOAPSchema() {
        return SchemaBuilder.builder().record("SOAP").fields() //
                .name(FIELD_LIST_KEY_TYPE)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().stringType().noDefault() //
                .name(FIELD_LIST_KEY_VALUE)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().stringType().noDefault() //
                .name(FIELD_LEAD_KEY_TYPE)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().stringType().noDefault() //
                .name(FIELD_LEAD_KEY_VALUE)//
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true")//
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true")//
                .type().stringType().noDefault() //
                .endRecord();
    }

    /**
     * Custom Objects
     */
    public static Schema getCustomObjectRecordSchema() {
        return record("CustomObjectRecord").fields()//
                .name("marketoGUID").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().stringType().noDefault()//
                .name("seq").type().intType().noDefault()//
                .name("createdAt") //
                .prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'Z'")//
                .prop(SchemaConstants.JAVA_CLASS_FLAG, Date.class.getCanonicalName()) //
                .type().nullable().longType().noDefault()//
                .name("updatedAt") //
                .prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'Z'")//
                .prop(SchemaConstants.JAVA_CLASS_FLAG, Date.class.getCanonicalName()) //
                .type().nullable().longType().noDefault()//
                .name("fields").type().nullable().stringType().noDefault()// ObjectField[]
                //
                .endRecord();
    }

    /**
     * Lead Schemas
     */

    public static Schema getSOAPOuputSchemaForSyncLead() {
        return SchemaBuilder.builder().record("syncLead").fields() //
                .name("Id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().nullable().intType().noDefault() //
                .name("Email").type().nullable().stringType().noDefault() //
                .name("ForeignSysPersonId").type().nullable().stringType().noDefault() //
                .name("ForeignSysType").type().nullable().stringType().noDefault() //
                .endRecord();
    }

    public static Schema getRESTOutputSchemaForSyncLead() {
        return SchemaBuilder.builder().record("syncLead").fields() //
                .name("id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().nullable().intType().noDefault() //
                .name("email").type().nullable().stringType().noDefault() //
                .name("firstName").type().nullable().stringType().noDefault() //
                .name("lastName").type().nullable().stringType().noDefault() //
                .endRecord();
    }

    /////////////////////////////////////////////////////////////////////////
    // REST
    /////////////////////////////////////////////////////////////////////////
    public static Schema getRESTSchemaForGetLeadOrGetMultipleLeads() {
        return SchemaBuilder.builder().record("getLeadOrGetMultipleLeads").fields() //
                .name("id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().nullable().intType().noDefault() //
                .name("email").type().nullable().stringType().noDefault() //
                .name("firstName").type().nullable().stringType().noDefault() //
                .name("lastName").type().nullable().stringType().noDefault() //
                .name("createdAt")//
                .prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'Z'")//
                .prop(SchemaConstants.JAVA_CLASS_FLAG, Date.class.getCanonicalName()) //
                .type(AvroUtils._date()).noDefault()//
                .name("updatedAt")//
                .prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'Z'")//
                .prop(SchemaConstants.JAVA_CLASS_FLAG, Date.class.getCanonicalName()) //
                .type(AvroUtils._date()).noDefault()//
                .endRecord();
    }

    public static Schema getRESTSchemaForGetLeadChanges() {
        return SchemaBuilder.builder().record("getLeadChanges").fields() //
                .name("id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().nullable().intType().noDefault() //
                .name("leadId").type().nullable().intType().noDefault() //
                .name("activityDate")//
                .prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'Z'")//
                .prop(SchemaConstants.JAVA_CLASS_FLAG, Date.class.getCanonicalName()) //
                .type(AvroUtils._date()).noDefault()//
                .name("activityTypeId").type().nullable().intType().noDefault() //
                .name("activityTypeValue").type().nullable().stringType().noDefault() //
                .name("fields").type().nullable().stringType().noDefault() //
                .endRecord();
    }

    public static Schema getRESTSchemaForGetLeadActivity() {
        return SchemaBuilder.builder().record("getLeadActivity").fields() //
                .name("id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().nullable().intType().noDefault() //
                .name("leadId").type().nullable().intType().noDefault() //
                .name("activityDate")//
                .prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'Z'")//
                .prop(SchemaConstants.JAVA_CLASS_FLAG, Date.class.getCanonicalName()) //
                .type(AvroUtils._date()).noDefault()//
                .name("activityTypeId").type().nullable().intType().noDefault() //
                .name("activityTypeValue").type().nullable().stringType().noDefault() //
                .name("primaryAttributeValueId").type().nullable().intType().noDefault() //
                .name("primaryAttributeValue").type().nullable().stringType().noDefault() //
                .endRecord();
    }

    //////////////////////////////////////////////////////////////////////////
    // SOAP
    /////////////////////////////////////////////////////////////////////////
    public static Schema getSOAPSchemaForGetLeadOrGetMultipleLeads() {
        return SchemaBuilder.builder().record("getLeadOrGetMultipleLeads").fields() //
                .name("Id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().nullable().longType().noDefault() //
                .name("Email").type().nullable().stringType().noDefault() //
                .name("ForeignSysPersonId").type().nullable().stringType().noDefault() //
                .name("ForeignSysType").type().nullable().stringType().noDefault() //
                .endRecord();
    }

    public static Schema getSOAPSchemaForGetLeadChanges() {
        return SchemaBuilder.builder().record("getLeadChanges").fields() //
                .name("Id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().nullable().longType().noDefault() //
                .name("ActivityDateTime")//
                .prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'.000Z'")//
                .prop(SchemaConstants.JAVA_CLASS_FLAG, Date.class.getCanonicalName()) //
                .prop(SchemaConstants.TALEND_COLUMN_DB_LENGTH, "255")//
                .type(AvroUtils._date()).noDefault()//
                .name("ActivityType").type().nullable().stringType().noDefault() //
                .name("MktgAssetName").type().nullable().stringType().noDefault() //
                .name("MktPersonId").type().nullable().stringType().noDefault() //
                .name("Campaign").type().nullable().stringType().noDefault() //
                .endRecord();
    }

    public static Schema getSOAPSchemaForGetLeadActivity() {
        return SchemaBuilder.builder().record("getLeadActivity").fields() //
                .name("Id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().nullable().longType().noDefault() //
                .name("ActivityDateTime")//
                .prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'.000Z'")
                .prop(SchemaConstants.JAVA_CLASS_FLAG, Date.class.getCanonicalName()) //
                .prop(SchemaConstants.TALEND_COLUMN_DB_LENGTH, "255")//
                .type(AvroUtils._date()).noDefault()//
                .name("ActivityType").type().nullable().stringType().noDefault() //
                .name("MktgAssetName").type().nullable().stringType().noDefault() //
                .name("MktPersonId").type().nullable().stringType().noDefault() //
                .name("Campaign").type().nullable().stringType().noDefault() //
                .name("ForeignSysId").type().nullable().stringType().noDefault() //
                .name("PersonName").type().nullable().stringType().noDefault() //
                .name("OrgName").type().nullable().stringType().noDefault() //
                .name("ForeignSysOrgId").type().nullable().stringType().noDefault() //
                .endRecord();
    }

    /**
     * Other stuff
     */
    public static Schema getSampleSchema() {
        return SchemaBuilder.builder().record("getLeadActivity").fields() //
                .name("ActivityDateTime")//
                .prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'.000Z'")//
                .prop(SchemaConstants.JAVA_CLASS_FLAG, Date.class.getCanonicalName())//
                .type().nullable().longType().noDefault()//
                //
                .name("ActivityDateTime2")//
                .prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'.000Z'")//
                .prop(SchemaConstants.JAVA_CLASS_FLAG, Date.class.getCanonicalName()) //
                .prop(SchemaConstants.TALEND_COLUMN_DB_LENGTH, "255")//
                .type(AvroUtils._date()).noDefault()//
                .endRecord();
    }
}

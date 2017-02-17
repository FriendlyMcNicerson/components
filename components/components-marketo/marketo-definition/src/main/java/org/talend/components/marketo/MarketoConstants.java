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
import org.talend.daikon.avro.SchemaConstants;

/**
 * Common shared schemas and constants
 * 
 */
public class MarketoConstants {

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

}

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
package org.talend.components.common;

import static org.talend.components.api.properties.presentation.Widget.widget;
import static org.talend.components.api.schema.SchemaFactory.newSchemaElement;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.properties.presentation.Widget;
import org.talend.components.api.schema.Schema;
import org.talend.components.api.schema.SchemaElement;
import org.talend.components.api.schema.SchemaFactory;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("schema")
public class SchemaProperties extends ComponentProperties {

    //
    // Properties
    //
    public SchemaElement schema = newSchemaElement(SchemaElement.Type.SCHEMA, "schema", "Schema");

    public static final String MAIN = "Main";

    public static final String REFERENCE = "Reference";

    public SchemaProperties() {
        super();
        setValue(schema, SchemaFactory.newSchema());
        setupLayout();
    }

    @Override
    protected void setupLayout() {
        super.setupLayout();

        Form schemaForm = Form.create(this, MAIN, "Schema");
        schemaForm.addRow(widget(schema).setWidgetType(Widget.WidgetType.SCHEMA_EDITOR));
        refreshLayout(schemaForm);

        Form schemaRefForm = Form.create(this, REFERENCE, "Schema");
        schemaRefForm.addRow(widget(schema).setWidgetType(Widget.WidgetType.SCHEMA_REFERENCE));
        refreshLayout(schemaRefForm);
    }

    public SchemaElement addRow(SchemaElement row) {
        Schema s = (Schema) getValue(schema);
        if (s == null)
            s = SchemaFactory.newSchema();
        SchemaElement root = s.getRoot();
        if (root == null) {
            root = SchemaFactory.newSchemaElement("Root");
            s.setRoot(root);
        }
        root.addChild(row);
        return row;
    }

}
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
package org.talend.components.api.properties.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.components.api.ComponentDesigner;
import org.talend.components.api.properties.ValidationResult;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.schema.SchemaElement;

public class ComponentPropertiesInternal {

    protected String name;

    protected String title;

    protected ComponentDesigner designer;

    protected boolean runtimeOnly;

    protected List<Form> forms;

    protected ValidationResult validationResult;

    protected Map<SchemaElement, Object> propertyValues;

    public ComponentPropertiesInternal() {
        forms = new ArrayList<Form>();
        propertyValues = new HashMap<>();
    }

    public void setRuntimeOnly() {
        runtimeOnly = true;
    }

    public boolean isRuntimeOnly() {
        return runtimeOnly;
    }

    public List<Form> getForms() {
        return forms;
    }

    public Form getForm(String name) {
        for (Form f : forms) {
            if (f.getName().equals(name))
                return f;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setValue(SchemaElement property, Object value) {
        propertyValues.put(property, value);
    }

    public Object getValue(SchemaElement property) {
        return propertyValues.get(property);
    }

    public ComponentDesigner getDesigner() {
        return designer;
    }

    public void setDesigner(ComponentDesigner designer) {
        this.designer = designer;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }
}

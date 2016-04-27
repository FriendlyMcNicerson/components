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
package org.talend.components.jira.tjirainput;

import java.util.Collections;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.HasSchemaProperty;
import org.talend.components.common.SchemaProperties;
import org.talend.components.common.UserPasswordProperties;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.PropertyFactory;
import org.talend.daikon.properties.presentation.Form;

/**
 * {@link Properties} for Jira input component.
 * They consists of following properties:
 * Jira hostUrl
 * Jira resource
 * schema
 * authorization type
 * 
 * created by ivan.honchar on Apr 22, 2016
 */
public class TJiraInputProperties extends ComponentProperties implements HasSchemaProperty {
    
    /**
     * Jira issue resource value
     */
    public static final String ISSUE = "issue";
    
    /**
     * Jira project resource value
     */
    public static final String PROJECT = "project";
    
    /**
     * Basic http authorization type
     */
    private static final String BASIC = "Basic";

    /**
     * OAuth http authorization type
     */
    private static final String OAUTH = "OAuth";
    
    /**
     * UserPassword properties name
     */
    private static final String USERPASSWORD = "userPassword";
    
    /**
     * URL of Jira instance
     */
    public Property hostUrl = PropertyFactory.newString("hostUrl");
    
    /**
     * Jira resource. This may be issue, project etc.
     * TODO clarify, which resources to support. Find solution to support variable set of resources.
     * maybe Property.Type.String
     */
    public Property resource = PropertyFactory.newEnum("resource", ISSUE, PROJECT);
    
    /**
     * Jira Query language request property
     */
    public Property jql = PropertyFactory.newString("jql");
    
    /**
     * Type of http authorization.
     * TODO maybe move it to Connection properties class and maybe in components-common
     */
    public Property authorizationType = PropertyFactory.newEnum("authorizationType", BASIC, OAUTH);
    
    /**
     * User id and password properties for Basic Authorization
     */
    public UserPasswordProperties userPassword = new UserPasswordProperties(USERPASSWORD);
    
    /**
     * Schema property to define required fields of Jira resource
     */
    public SchemaProperties schema = new SchemaProperties("schema");
    
    public TJiraInputProperties(String name) {
        super(name);
    }

    @Override
    public List<Schema> getSchemas() {
        return Collections.singletonList(new Schema.Parser().parse(schema.schema.getStringValue()));
    }

    @Override
    public void setSchemas(List<Schema> schemas) {
        // nothing to be set here.
    }
    
    @Override
    public void setupProperties() {
        super.setupProperties();
        hostUrl.setValue("\"https://localhost:8080/\"");
        resource.setValue(ISSUE);
        authorizationType.setValue(BASIC);
        jql.setValue("\"\"");
    }
    
    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN, "Jira Input");
        form.addRow(schema.getForm(Form.REFERENCE));
        form.addRow(hostUrl);
        form.addRow(resource);
        form.addRow(jql);
        form.addRow(authorizationType);
        form.addRow(userPassword.getForm(Form.MAIN));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        
        if (form.getName().equals(Form.MAIN)) {
            if(OAUTH.equals(authorizationType.getStringValue())) {
                form.getWidget(USERPASSWORD).setVisible(false);
            }
            if(PROJECT.equals(resource.getStringValue())) {
                form.getWidget(jql.getName()).setVisible(false);
            }
        }
    }

}

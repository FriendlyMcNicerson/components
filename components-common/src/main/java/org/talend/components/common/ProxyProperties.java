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

import org.talend.components.api.ComponentProperties;
import org.talend.components.api.properties.Property;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.properties.presentation.Layout;

public class ProxyProperties extends ComponentProperties {

    public Property<Boolean>      useProxy     = new Property<Boolean>("useProxy", "Use Proxy").setRequired(true);

    public Property<String>       host         = new Property<String>("host", "Host").setRequired(true);

    public UserPasswordProperties userPassword = new UserPasswordProperties();

    public static final String    PROXY        = "Proxy";

    public ProxyProperties() {
        Form form = new Form(this, PROXY, "Proxy Parameters");
        form.addChild(useProxy, Layout.create().setRow(1));
        form.addChild(host, Layout.create().setRow(2));
        form.addChild(userPassword.getForm(UserPasswordProperties.USERPASSWORD), Layout.create().setRow(3));
    }

}

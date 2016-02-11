package org.talend.components.mysql.tMysqlInput;

import org.talend.components.mysql.metadata.MysqlMetadataProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.PropertyFactory;

/**
 * Created by bchen on 16-1-18.
 */
public class tMysqlInputProperties extends MysqlMetadataProperties {

    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public tMysqlInputProperties(String name) {
        super(name);
    }

    public Property QUERY = PropertyFactory.newString("QUERY", "select id, name from employee");
}

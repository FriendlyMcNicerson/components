package org.talend.components.cassandra.tCassandraConfiguration;

import org.talend.components.cassandra.CassandraProperties;
import org.talend.daikon.properties.Property;

import static org.talend.daikon.properties.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.PropertyFactory.newString;

/**
 * Created by bchen on 16-1-11.
 */
public class tCassandraConfigurationProperties extends CassandraProperties {
    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public tCassandraConfigurationProperties(String name) {
        super(name);
    }

    public Property host = newString("host");

    public Property port = newString("port");

    public Property useAuth = newBoolean("useAuth", false);

    public Property username = newString("username");

    public Property password = newString("password");


}

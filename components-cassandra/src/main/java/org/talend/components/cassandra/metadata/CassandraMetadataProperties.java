package org.talend.components.cassandra.metadata;

import org.talend.components.cassandra.mako.tCassandraConnectionProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.PropertyFactory;

/**
 * Created by bchen on 16-1-17.
 */
public class CassandraMetadataProperties extends tCassandraConnectionProperties {

    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public CassandraMetadataProperties(String name) {
        super(name);
    }

    public Property keyspace = PropertyFactory.newString("keyspace");

    public Property columnFamily = PropertyFactory.newString("columnFamily");

    public SchemaProperties schema = new SchemaProperties("schema");
}

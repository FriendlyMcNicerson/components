package org.talend.components.snowflake.runtime;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.common.avro.JDBCResultSetIndexedRecordConverter;
import org.talend.components.snowflake.SnowflakeConnectionTableProperties;
import org.talend.components.snowflake.connection.SnowflakeNativeConnection;
import org.talend.components.snowflake.tsnowflakeinput.TSnowflakeInputProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.converter.IndexedRecordConverter;

public class SnowflakeReader<T> extends AbstractBoundedReader<IndexedRecord> {

    private transient SnowflakeNativeConnection connection;

    private transient JDBCResultSetIndexedRecordConverter factory;

    protected TSnowflakeInputProperties properties;

    protected int dataCount;

    private RuntimeContainer container;

    protected ResultSet resultSet;

    private transient Schema querySchema;

    private Statement statement;

    private Result result;


    public SnowflakeReader(RuntimeContainer container, BoundedSource source, TSnowflakeInputProperties props) throws IOException {
        super(source);
        this.container = container;
        this.properties = props;
        factory = new JDBCResultSetIndexedRecordConverter();
        factory.setSchema(getSchema());
    }

    protected SnowflakeNativeConnection getConnection() throws IOException {
        if (null == connection) {
            connection = ((SnowflakeSource) getCurrentSource()).connect(container);
        }
        return connection;
    }

    protected Schema getSchema() throws IOException {
        if (querySchema == null) {
            querySchema = properties.table.main.schema.getValue();
            if (AvroUtils.isIncludeAllFields(querySchema)) {
                String tableName = null;
                if (properties instanceof SnowflakeConnectionTableProperties) {
                    tableName = properties.table.tableName.getStringValue();
                }
                querySchema = getCurrentSource().getEndpointSchema(container, tableName);
            }
        }
        return querySchema;
    }

    protected String getQueryString() throws IOException {
        String condition = null;
        if (properties.manualQuery.getValue()) {
            return properties.query.getStringValue();
        } else {
            condition = properties.condition.getStringValue();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("select "); //$NON-NLS-1$
        int count = 0;
        for (Schema.Field se : getSchema().getFields()) {
            if (count++ > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(se.name());
        }
        sb.append(" from "); //$NON-NLS-1$
        sb.append(properties.table.tableName.getStringValue());
        if (condition != null && condition.trim().length() > 0) {
            sb.append(" where ");
            sb.append(condition);
        }
        return sb.toString();
    }


    @Override
    public boolean start() throws IOException {
        result = new Result();
        try {
            statement = getConnection().getConnection().createStatement();
            resultSet = statement.executeQuery(getQueryString());
            return haveNext();
        } catch (Exception e) {
            throw new ComponentException(e);
        }
    }

    private boolean haveNext() throws SQLException {
        boolean haveNext = resultSet.next();

        if (haveNext) {
            result.totalCount++;
        }

        return haveNext;
    }

    @Override
    public boolean advance() throws IOException {
        try {
            return haveNext();
        } catch (SQLException e) {
            throw new ComponentException(e);
        }
    }

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        try {
            return factory.convertToAvro(resultSet);
        } catch (Exception e) {
            throw new ComponentException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }

            if (statement != null) {
                statement.close();
                statement = null;
            }

            if (connection != null) {
                getConnection().getConnection().close();
            }
        } catch (SQLException e) {
            throw new ComponentException(e);
        }
    }

    @Override
    public Map<String, Object> getReturnValues() {
        Result result = new Result();
        result.totalCount = dataCount;
        return result.toMap();
    }


}

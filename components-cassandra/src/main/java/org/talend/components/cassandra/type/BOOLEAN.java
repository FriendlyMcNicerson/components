package org.talend.components.cassandra.type;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.talend.components.api.schema.column.type.TBoolean;

/**
 * Created by bchen on 16-1-10.
 */
public class BOOLEAN extends CassandraBaseType<Boolean, TBoolean> {

    @Override
    protected Boolean getAppValue(Row app, String key) {
        return app.getBool(key);
    }

    @Override
    protected void setAppValue(BoundStatement app, String key, Boolean value) {
        app.setBool(key, value);
    }

    @Override
    protected Boolean convert2AType(TBoolean value) {
        return value.getValue();
    }

    @Override
    protected TBoolean convert2TType(Boolean value) {
        TBoolean v = new TBoolean();
        v.setValue(value);
        return v;
    }
}

package org.talend.components.jdbc.common;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;

public class ComplexDBTableWithAllDataType {

    // TODO : now we have to use the type for derby to test, should use the common one for every database or write it for every
    // database
    public static void createTestTable(Connection conn) throws SQLException {
        try (Statement statement = conn.createStatement()) {
            statement.execute(
                    "CREATE TABLE TEST (C1 INT, C2 SMALLINT, C3 BIGINT, C4 REAL,C5 DOUBLE, C6 FLOAT, C7 DECIMAL(10,2), C8 NUMERIC(10,2), C9 BOOLEAN, C10 CHAR(64), C11 DATE, C12 TIME, C13 TIMESTAMP, C14 VARCHAR(64), C15 LONG VARCHAR, C16 BLOB(16), C17 CLOB(16) )");
        }
    }
    
    public static String getSQL() {
        return "SELECT C1,C2,C3,C4,C5,C6,C7,C8,C9,C10,C11,C12,C13,C14,C15 FROM TEST";
    }

    public static Schema createTestSchema(boolean nullableForAnyColumn, boolean withLobTypes) {
        FieldAssembler<Schema> builder = SchemaBuilder.builder().record("TEST").fields();

        Schema schema = AvroUtils._int();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C1").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C1")
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type(schema).noDefault();

        schema = AvroUtils._short();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C2").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C2").type(schema).noDefault();

        schema = AvroUtils._long();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C3").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C3").type(schema).noDefault();

        schema = AvroUtils._float();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C4").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C4").type(schema).noDefault();

        schema = AvroUtils._double();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C5").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C5").type(schema).noDefault();

        schema = AvroUtils._float();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C6").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C6").type(schema).noDefault();

        schema = AvroUtils._decimal();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C7").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C7").type(schema).noDefault();

        schema = AvroUtils._decimal();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C8").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C8").type(schema).noDefault();

        schema = AvroUtils._boolean();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C9").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C9").type(schema).noDefault();

        schema = AvroUtils._string();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C10").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C10").type(schema).noDefault();

        // AvroUtils.logicalDate, logicalTime and logicalTimestamp is not stable now, so use AvroUtils._date()
        // schema = AvroUtils._logicalDate();
        schema = AvroUtils._date();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C11").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C11").type(schema).noDefault();

        // schema = AvroUtils._logicalTime();
        schema = AvroUtils._date();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C12").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C12").type(schema).noDefault();

        // schema = AvroUtils._logicalTimestamp();
        schema = AvroUtils._date();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C13").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C13").type(schema).noDefault();

        schema = AvroUtils._string();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C14").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C14").type(schema).noDefault();

        schema = AvroUtils._string();
        schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
        builder = builder.name("C15").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C15").type(schema).noDefault();

        if (withLobTypes) {
            schema = AvroUtils._bytes();
            schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
            builder = builder.name("C16").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C16").type(schema).noDefault();

            schema = AvroUtils._string();
            schema = SchemaUtils.wrap(schema, nullableForAnyColumn);
            builder = builder.name("C17").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "C17").type(schema).noDefault();
        }

        return builder.endRecord();

    }

    public static Schema createTestSchema(boolean nullableForAnyColumn) {
        return createTestSchema(nullableForAnyColumn, true);
    }

    public static void loadTestData(Connection conn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("insert into TEST values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
            statement.setInt(1, 1);
            statement.setShort(2, (short) 2);
            statement.setLong(3, 3l);
            statement.setFloat(4, 4f);
            statement.setDouble(5, 5d);
            statement.setFloat(6, 6f);
            statement.setBigDecimal(7, new BigDecimal("7.01"));
            statement.setBigDecimal(8, new BigDecimal("8.01"));
            statement.setBoolean(9, true);
            statement.setString(10, "the first char value");
            long currentTimeMillis = System.currentTimeMillis();
            statement.setTimestamp(11, new java.sql.Timestamp(currentTimeMillis));
            statement.setTimestamp(12, new java.sql.Timestamp(currentTimeMillis));
            statement.setTimestamp(13, new java.sql.Timestamp(currentTimeMillis));
            statement.setString(14, "wangwei");
            statement.setString(15, "a long one : 1");
            setLob(conn, statement);
            statement.executeUpdate();

            statement.setInt(1, 1);
            statement.setShort(2, (short) 2);
            statement.setLong(3, 3l);
            statement.setFloat(4, 4f);
            statement.setDouble(5, 5d);
            statement.setFloat(6, 6f);
            statement.setBigDecimal(7, new BigDecimal("7.01"));
            statement.setBigDecimal(8, new BigDecimal("8.01"));
            statement.setBoolean(9, true);
            statement.setString(10, "the second char value");
            statement.setTimestamp(11, new java.sql.Timestamp(currentTimeMillis));
            statement.setTimestamp(12, new java.sql.Timestamp(currentTimeMillis));
            statement.setTimestamp(13, new java.sql.Timestamp(currentTimeMillis));
            statement.setString(14, "gaoyan");
            statement.setString(15, "a long one : 2");
            setLob(conn, statement);
            statement.executeUpdate();

            statement.setInt(1, 1);
            statement.setShort(2, (short) 2);
            statement.setLong(3, 3l);
            statement.setFloat(4, 4f);
            statement.setDouble(5, 5d);
            statement.setFloat(6, 6f);
            statement.setBigDecimal(7, new BigDecimal("7.01"));
            statement.setBigDecimal(8, new BigDecimal("8.01"));
            statement.setBoolean(9, true);
            statement.setString(10, "the third char value");
            statement.setTimestamp(11, new java.sql.Timestamp(currentTimeMillis));
            statement.setTimestamp(12, new java.sql.Timestamp(currentTimeMillis));
            statement.setTimestamp(13, new java.sql.Timestamp(currentTimeMillis));
            statement.setString(14, "dabao");
            statement.setString(15, "a long one : 3");
            setLob(conn, statement);
            statement.executeUpdate();

            statement.setInt(1, 2147483647);
            statement.setShort(2, (short) 32767);
            statement.setLong(3, 9223372036854775807l);
            statement.setNull(4, java.sql.Types.FLOAT);
            statement.setNull(5, java.sql.Types.DOUBLE);
            statement.setNull(6, java.sql.Types.FLOAT);
            statement.setNull(7, java.sql.Types.DECIMAL);
            statement.setNull(8, java.sql.Types.DECIMAL);
            statement.setNull(9, java.sql.Types.BOOLEAN);
            statement.setNull(10, java.sql.Types.CHAR);
            statement.setDate(11, Date.valueOf("2016-12-28"));
            statement.setTime(12, Time.valueOf("14:30:33"));
            statement.setTimestamp(13, Timestamp.valueOf("2016-12-28 14:31:56.12345"));
            statement.setNull(14, java.sql.Types.VARCHAR);
            statement.setNull(15, java.sql.Types.LONGVARCHAR);
            statement.setNull(16, java.sql.Types.BLOB);
            statement.setNull(17, java.sql.Types.CLOB);
            statement.executeUpdate();

            // used by testing the null value
            statement.setInt(1, 1);
            statement.setNull(2, java.sql.Types.SMALLINT);
            statement.setNull(3, java.sql.Types.BIGINT);
            statement.setNull(4, java.sql.Types.FLOAT);
            statement.setNull(5, java.sql.Types.DOUBLE);
            statement.setNull(6, java.sql.Types.FLOAT);
            statement.setNull(7, java.sql.Types.DECIMAL);
            statement.setNull(8, java.sql.Types.DECIMAL);
            statement.setNull(9, java.sql.Types.BOOLEAN);
            statement.setNull(10, java.sql.Types.CHAR);
            statement.setNull(11, java.sql.Types.DATE);
            statement.setNull(12, java.sql.Types.TIME);
            statement.setNull(13, java.sql.Types.TIMESTAMP);
            statement.setNull(14, java.sql.Types.VARCHAR);
            statement.setNull(15, java.sql.Types.LONGVARCHAR);
            statement.setNull(16, java.sql.Types.BLOB);
            statement.setNull(17, java.sql.Types.CLOB);
            statement.executeUpdate();

            statement.setNull(1, java.sql.Types.INTEGER);
            statement.setNull(2, java.sql.Types.SMALLINT);
            statement.setNull(3, java.sql.Types.BIGINT);
            statement.setNull(4, java.sql.Types.FLOAT);
            statement.setNull(5, java.sql.Types.DOUBLE);
            statement.setNull(6, java.sql.Types.FLOAT);
            statement.setNull(7, java.sql.Types.DECIMAL);
            statement.setNull(8, java.sql.Types.DECIMAL);
            statement.setNull(9, java.sql.Types.BOOLEAN);
            statement.setNull(10, java.sql.Types.CHAR);
            statement.setNull(11, java.sql.Types.DATE);
            statement.setNull(12, java.sql.Types.TIME);
            statement.setNull(13, java.sql.Types.TIMESTAMP);
            statement.setString(14, "good luck");
            statement.setNull(15, java.sql.Types.LONGVARCHAR);
            statement.setNull(16, java.sql.Types.BLOB);
            statement.setNull(17, java.sql.Types.CLOB);
            statement.executeUpdate();
        }

        if (!conn.getAutoCommit()) {
            conn.commit();
        }
    }

    private static void setLob(Connection conn, PreparedStatement statement) throws SQLException {
        Blob blob = conn.createBlob();
        byte[] bytes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        blob.setBytes(1, bytes);
        statement.setBlob(16, blob);

        Clob clob = conn.createClob();
        clob.setString(1, "a long content");
        statement.setClob(17, clob);
    }

    public static List<IndexedRecord> prepareIndexRecords(boolean nullableForAnyColumn) {
        List<IndexedRecord> result = new ArrayList<IndexedRecord>();

        Schema schema = createTestSchema(nullableForAnyColumn, false);

        IndexedRecord r = new GenericData.Record(schema);
        r.put(0, 1);
        r.put(1, (short) 2);
        r.put(2, 3l);
        r.put(3, 4f);
        r.put(4, 5d);
        r.put(5, 6f);
        r.put(6, new BigDecimal("7.01"));
        r.put(7, new BigDecimal("8.01"));
        r.put(8, true);
        r.put(9, "content : 1");
        r.put(10, new java.util.Date());
        r.put(11, new java.util.Date());
        r.put(12, new java.util.Date());
        r.put(13, "wangwei");
        r.put(14, "long content : 1");
        result.add(r);

        r = new GenericData.Record(schema);
        r.put(0, 1);
        r.put(1, (short) 2);
        r.put(2, 3l);
        r.put(3, 4f);
        r.put(4, 5d);
        r.put(5, 6f);
        r.put(6, new BigDecimal("7.01"));
        r.put(7, new BigDecimal("8.01"));
        r.put(8, true);
        r.put(9, "content : 2");
        r.put(10, new java.util.Date());
        r.put(11, new java.util.Date());
        r.put(12, new java.util.Date());
        r.put(13, "gaoyan");
        r.put(14, "long content : 2");
        result.add(r);

        r = new GenericData.Record(schema);
        r.put(0, 1);
        r.put(1, (short) 2);
        r.put(2, 3l);
        r.put(3, 4f);
        r.put(4, 5d);
        r.put(5, 6f);
        r.put(6, new BigDecimal("7.01"));
        r.put(7, new BigDecimal("8.01"));
        r.put(8, true);
        r.put(9, "content : 3");
        r.put(10, new java.util.Date());
        r.put(11, new java.util.Date());
        r.put(12, new java.util.Date());
        r.put(13, "dabao");
        r.put(14, "long content : 3");
        result.add(r);

        // used by testing the null value
        r = new GenericData.Record(schema);
        r.put(0, 1);
        r.put(1, null);
        r.put(2, null);
        r.put(3, null);
        r.put(4, null);
        r.put(5, null);
        r.put(6, null);
        r.put(7, null);
        r.put(8, null);
        r.put(9, null);
        r.put(10, null);
        r.put(11, null);
        r.put(12, null);
        r.put(13, null);
        r.put(14, null);
        result.add(r);

        r = new GenericData.Record(schema);
        r.put(0, null);
        r.put(1, null);
        r.put(2, null);
        r.put(3, null);
        r.put(4, null);
        r.put(5, null);
        r.put(6, null);
        r.put(7, null);
        r.put(8, null);
        r.put(9, null);
        r.put(10, null);
        r.put(11, null);
        r.put(12, null);
        r.put(13, "good luck");
        r.put(14, null);
        result.add(r);

        return result;
    }

    // we comment the assert which may be database special
    public static void testMetadata(List<Field> columns, boolean allString) {
        Schema.Field field = columns.get(0);

        assertEquals("C1", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(allString ? AvroUtils._string() : AvroUtils._int(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.INTEGER, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        // assertEquals(10, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(1);

        assertEquals("C2", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(allString ? AvroUtils._string() : AvroUtils._short(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.SMALLINT, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        // assertEquals(5, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(2);

        assertEquals("C3", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(allString ? AvroUtils._string() : AvroUtils._long(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.BIGINT, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        // assertEquals(19, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(3);

        assertEquals("C4", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        // assertEquals(allString ? AvroUtils._string() : AvroUtils._float(), AvroUtils.unwrapIfNullable(field.schema()));
        // assertEquals(java.sql.Types.REAL, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        // assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(4);

        assertEquals("C5", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(allString ? AvroUtils._string() : AvroUtils._double(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.DOUBLE, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(5);

        assertEquals("C6", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        // assertEquals(allString ? AvroUtils._string() : AvroUtils._double(), AvroUtils.unwrapIfNullable(field.schema()));
        // assertEquals(java.sql.Types.DOUBLE, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(6);

        assertEquals("C7", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(allString ? AvroUtils._string() : AvroUtils._decimal(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.DECIMAL, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(10, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(2, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(7);

        assertEquals("C8", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(allString ? AvroUtils._string() : AvroUtils._decimal(), AvroUtils.unwrapIfNullable(field.schema()));
        // assertEquals(java.sql.Types.NUMERIC, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(10, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(2, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(8);

        assertEquals("C9", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        // assertEquals(allString ? AvroUtils._string() : AvroUtils._boolean(), AvroUtils.unwrapIfNullable(field.schema()));
        // assertEquals(java.sql.Types.BOOLEAN, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(9);

        assertEquals("C10", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(AvroUtils._string(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.CHAR, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(64, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(10);

        assertEquals("C11", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(allString ? AvroUtils._string() : AvroUtils._date(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.DATE, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals("yyyy-MM-dd", field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(11);

        assertEquals("C12", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(allString ? AvroUtils._string() : AvroUtils._date(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.TIME, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals("HH:mm:ss", field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(12);

        assertEquals("C13", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(allString ? AvroUtils._string() : AvroUtils._date(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.TIMESTAMP, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals("yyyy-MM-dd HH:mm:ss.SSS", field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        // assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(13);

        assertEquals("C14", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(AvroUtils._string(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.VARCHAR, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        assertEquals(64, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));

        field = columns.get(14);

        assertEquals("C15", field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME));
        assertEquals(AvroUtils._string(), AvroUtils.unwrapIfNullable(field.schema()));
        assertEquals(java.sql.Types.LONGVARCHAR, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_TYPE));
        // assertEquals(32700, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PRECISION));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_SCALE));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertEquals(null, field.getObjectProp(SchemaConstants.TALEND_COLUMN_DEFAULT));
    }

}

package org.talend.components.filedelimited.runtime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.common.ComponentConstants;
import org.talend.components.common.runtime.FormatterUtils;
import org.talend.components.common.runtime.ParserUtils;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.converter.AvroConverter;
import org.talend.daikon.java8.SerializableFunction;

public class FileDelimitedAvroRegistry extends AvroRegistry {

    private static FileDelimitedAvroRegistry fileDelimitedInstance;

    public FileDelimitedAvroRegistry() {

        registerSchemaInferrer(IndexedRecord.class, new SerializableFunction<IndexedRecord, Schema>() {

            /** Default serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public Schema apply(IndexedRecord t) {
                return inferSchemaRecord(t);
            }

        });

        registerSchemaInferrer(Schema.Field.class, new SerializableFunction<Schema.Field, Schema>() {

            /** Default serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public Schema apply(Schema.Field t) {
                return inferSchemaField(t);
            }

        });
    }

    public static FileDelimitedAvroRegistry get() {
        if (fileDelimitedInstance == null) {
            fileDelimitedInstance = new FileDelimitedAvroRegistry();
        }
        return fileDelimitedInstance;
    }

    private Schema inferSchemaRecord(IndexedRecord in) {
        return in.getSchema();
    }

    /**
     * Infers an Avro schema for the given Salesforce Field. This can be an expensive operation so the schema should be
     * cached where possible. The return type will be the Avro Schema that can contain the field data without loss of
     * precision.
     *
     * @param field the Field to analyse.
     * @return the schema for data that the field describes.
     */
    private Schema inferSchemaField(Schema.Field field) {
        return field.schema();
    }

    public AvroConverter<String, ?> getConverter(org.apache.avro.Schema.Field f) {
        Schema fieldSchema = AvroUtils.unwrapIfNullable(f.schema());
        if (AvroUtils.isSameType(fieldSchema, AvroUtils._boolean())) {
            return new BooleanConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._decimal())) {
            return new DecimalConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._double())) {
            return new DoubleConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._float())) {
            return new FloatConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._int())) {
            return new IntegerConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._date())) {
            return new DateConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._long())) {
            return new LongConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._bytes())) {
            return new BytesConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._byte())) {
            return new ByteConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._short())) {
            return new ShortConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._character())) {
            return new CharacterConverter(f);
        } else if (AvroUtils.isSameType(fieldSchema, AvroUtils._string())) {
            return super.getConverter(String.class);
        }
        throw new UnsupportedOperationException("The type " + fieldSchema.getType() + " is not supported."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static abstract class StringConverter<T> implements AvroConverter<String, T> {

        protected final Schema.Field field;

        StringConverter(Schema.Field field) {
            this.field = field;
        }

        @Override
        public Schema getSchema() {
            return field.schema();
        }

        @Override
        public Class<String> getDatumClass() {
            return String.class;
        }

        @Override
        public String convertToDatum(T value) {
            return value == null ? null : String.valueOf(value);
        }
    }

    public static abstract class NumberConverter<T> implements AvroConverter<String, T> {

        private final Schema.Field field;

        private final Character thousandsSepChar;

        private final Character decimalSepChar;

        protected boolean isDecode;

        NumberConverter(Schema.Field field) {
            this.field = field;
            this.isDecode = Boolean.valueOf(field.getProp(ComponentConstants.NUMBER_DECODE));
            this.thousandsSepChar = ParserUtils.parseToCharacter(field.getProp(ComponentConstants.THOUSANDS_SEPARATOR));
            this.decimalSepChar = ParserUtils.parseToCharacter(field.getProp(ComponentConstants.DECIMAL_SEPARATOR));
        }

        @Override
        public Schema getSchema() {
            return field.schema();
        }

        @Override
        public Class<String> getDatumClass() {
            return String.class;
        }

        @Override
        public String convertToDatum(T value) {
            // TODO check nullable?
            if (value == null) {
                return null;
            }

            if (thousandsSepChar != null || decimalSepChar != null) {
                return FormatterUtils.formatNumber(new BigDecimal(String.valueOf(value)).toPlainString(), thousandsSepChar,
                        decimalSepChar);
            } else {
                if (value instanceof BigDecimal) {
                    String precision = field.getProp(SchemaConstants.TALEND_COLUMN_PRECISION);
                    if (precision != null) {
                        return ((BigDecimal) value).setScale(Integer.valueOf(precision), RoundingMode.HALF_UP).toPlainString();
                    } else {
                        return ((BigDecimal) value).toPlainString();
                    }
                } else if (AvroUtils.isSameType(AvroUtils._decimal(), AvroUtils.unwrapIfNullable(field.schema()))) {
                    String precision = field.getProp(SchemaConstants.TALEND_COLUMN_PRECISION);
                    if (precision != null) {
                        return new BigDecimal(String.valueOf(value)).setScale(Integer.valueOf(precision), RoundingMode.HALF_UP)
                                .toPlainString();
                    }
                }
                return String.valueOf(value);
            }
        }

        /**
         * Transform number string with thousandsSepChar and decimalSepChar
         */
        protected String transformNumberString(String value) {
            if (thousandsSepChar != null && decimalSepChar != null) {
                return ParserUtils.transformNumberString(value, thousandsSepChar, decimalSepChar);
            }
            return value;
        }
    }

    public static class BooleanConverter extends StringConverter<Boolean> {

        BooleanConverter(Schema.Field field) {
            super(field);
        }

        @Override
        public Boolean convertToAvro(String value) {
            return ParserUtils.parseToBoolean(value);
        }
    }

    public static class DecimalConverter extends NumberConverter<BigDecimal> {

        DecimalConverter(Schema.Field field) {
            super(field);
        }

        @Override
        public BigDecimal convertToAvro(String value) {
            return StringUtils.isEmpty(value) ? null : new BigDecimal(transformNumberString(value));
        }
    }

    public static class DoubleConverter extends NumberConverter<Double> {

        DoubleConverter(Schema.Field field) {
            super(field);
        }

        @Override
        public Double convertToAvro(String value) {
            return StringUtils.isEmpty(value) ? null : Double.parseDouble(transformNumberString(value));
        }
    }

    public static class LongConverter extends NumberConverter<Long> {

        LongConverter(Schema.Field field) {
            super(field);
        }

        @Override
        public Long convertToAvro(String value) {
            return StringUtils.isEmpty(value) ? null : ParserUtils.parseToLong(transformNumberString(value), isDecode);
        }
    }

    public static class FloatConverter extends NumberConverter<Float> {

        FloatConverter(Schema.Field field) {
            super(field);
        }

        @Override
        public Float convertToAvro(String value) {
            return StringUtils.isEmpty(value) ? null : Float.parseFloat(transformNumberString(value));
        }
    }

    public static class DateConverter extends StringConverter<Object> {

        String pattern;

        boolean isLenient;

        DateConverter(Schema.Field field) {
            super(field);
            pattern = field.getProp(SchemaConstants.TALEND_COLUMN_PATTERN);
            isLenient = Boolean.parseBoolean(field.getProp(ComponentConstants.CHECK_DATE));
        }

        @Override
        public Long convertToAvro(String value) {
            Date date = null;
            if (!StringUtils.isEmpty(value)) {
                date = ParserUtils.parseToDate(value, pattern, !isLenient);
            }
            if (date != null) {
                return date.getTime();
            } else {
                return null;
            }
        }

        @Override
        public String convertToDatum(Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof Date) {
                return FormatterUtils.formatDate(((Date) value), pattern);
            } else {
                return FormatterUtils.formatDate(new Date((Long) value), pattern);
            }
        }

    }

    public static class IntegerConverter extends NumberConverter<Integer> {

        IntegerConverter(Schema.Field field) {
            super(field);
        }

        @Override
        public Integer convertToAvro(String value) {
            return StringUtils.isEmpty(value) ? null : ParserUtils.parseToInteger(transformNumberString(value), isDecode);
        }
    }

    public static class ShortConverter extends NumberConverter<Short> {

        ShortConverter(Schema.Field field) {
            super(field);
        }

        @Override
        public Short convertToAvro(String value) {
            return StringUtils.isEmpty(value) ? null : ParserUtils.parseToShort(transformNumberString(value), isDecode);
        }
    }

    public static class ByteConverter extends NumberConverter<Byte> {

        ByteConverter(Schema.Field field) {
            super(field);
            // This is for migration in TDI-29759
            if (field.getProp(ComponentConstants.NUMBER_DECODE) == null) {
                isDecode = true;
            }
        }

        @Override
        public Byte convertToAvro(String value) {
            return StringUtils.isEmpty(value) ? null : ParserUtils.parseToByte(transformNumberString(value), isDecode);
        }
    }

    public static class BytesConverter extends StringConverter<byte[]> {

        BytesConverter(Schema.Field field) {
            super(field);
        }

        @Override
        public byte[] convertToAvro(String value) {
            return StringUtils.isEmpty(value) ? null : value.getBytes();
        }

        @Override
        public String convertToDatum(byte[] value) {
            return value == null ? null
                    : Charset.forName(field.getProp(ComponentConstants.CHARSET_NAME)).decode(ByteBuffer.wrap(value)).toString();
        }

    }

    public static class CharacterConverter extends StringConverter<Character> {

        CharacterConverter(Schema.Field field) {
            super(field);
        }

        @Override
        public Character convertToAvro(String value) {
            return StringUtils.isEmpty(value) ? null : value.charAt(0);
        }
    }
}

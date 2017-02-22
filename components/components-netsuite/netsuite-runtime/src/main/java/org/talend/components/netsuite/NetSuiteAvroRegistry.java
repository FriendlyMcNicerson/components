package org.talend.components.netsuite;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.avro.Schema;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.netsuite.beans.EnumAccessor;
import org.talend.components.netsuite.client.model.BeanUtils;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.avro.converter.AvroConverter;

/**
 *
 */
public class NetSuiteAvroRegistry extends AvroRegistry {

    public static final String FAMILY_NAME = "NetSuite"; //$NON-NLS-1$

    private static final NetSuiteAvroRegistry instance = new NetSuiteAvroRegistry();

    private final DatatypeFactory datatypeFactory;

    private NetSuiteAvroRegistry() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ComponentException(e);
        }
    }

    public static NetSuiteAvroRegistry getInstance() {
        return instance;
    }

    public DatatypeFactory getDatatypeFactory() {
        return datatypeFactory;
    }

    public AvroConverter<?, ?> getConverter(Schema.Field field, Class<?> datumClass) {
        if (datumClass.isEnum()) {
            Class<Enum> enumClass = (Class<Enum>) datumClass;
            return new EnumToStringConverter<>(field, enumClass, BeanUtils.getEnumAccessor(enumClass));
        }
        if (datumClass == XMLGregorianCalendar.class) {
            return new XMLGregorianCalendarToTimestampConverter(field, datatypeFactory);
        }
        return super.getConverter(datumClass);
    }

    /**
     * @return The family that uses the specific objects that this converter knows how to translate.
     */
    public String getFamilyName() {
        return FAMILY_NAME;
    }

    public static class EnumToStringConverter<T extends Enum<T>> implements AvroConverter<T, String> {

        private final Schema.Field field;
        private final Class<T> clazz;
        private final EnumAccessor enumAccessor;

        public EnumToStringConverter(Schema.Field field, Class<T> clazz,
                EnumAccessor enumAccessor) {
            this.field = field;
            this.clazz = clazz;
            this.enumAccessor = enumAccessor;
        }

        @Override
        public Schema getSchema() {
            return field.schema();
        }

        @Override
        public Class<T> getDatumClass() {
            return clazz;
        }

        @Override
        public T convertToDatum(String value) {
            if (value == null) {
                return null;
            }
            try {
                return (T) enumAccessor.mapFromString(value);
            } catch (IllegalArgumentException ex) {
                // Fallback to .valueOf(String)
                return Enum.valueOf(clazz, value);
            }
        }

        @Override
        public String convertToAvro(Enum enumValue) {
            if (enumValue == null) {
                return null;
            }
            try {
                return enumAccessor.mapToString(enumValue);
            } catch (IllegalArgumentException ex) {
                // Fallback to .name()
                return enumValue.name();
            }
        }
    }

    public static class XMLGregorianCalendarToTimestampConverter implements AvroConverter<XMLGregorianCalendar, Long> {

        private final Schema.Field field;

        private DatatypeFactory datatypeFactory;

        public XMLGregorianCalendarToTimestampConverter(Schema.Field field, DatatypeFactory datatypeFactory) {
            this.field = field;
            this.datatypeFactory = datatypeFactory;
        }

        @Override
        public Schema getSchema() {
            return field.schema();
        }

        @Override
        public Class<XMLGregorianCalendar> getDatumClass() {
            return XMLGregorianCalendar.class;
        }

        @Override
        public XMLGregorianCalendar convertToDatum(Long timestamp) {
            if (timestamp == null) {
                return null;
            }

            MutableDateTime dateTime = new MutableDateTime();
            dateTime.setMillis(timestamp);

            XMLGregorianCalendar xts = datatypeFactory.newXMLGregorianCalendar();
            xts.setYear(dateTime.getYear());
            xts.setMonth(dateTime.getMonthOfYear());
            xts.setDay(dateTime.getDayOfMonth());
            xts.setHour(dateTime.getHourOfDay());
            xts.setMinute(dateTime.getMinuteOfHour());
            xts.setSecond(dateTime.getSecondOfMinute());
            xts.setMillisecond(dateTime.getMillisOfSecond());
            xts.setTimezone(dateTime.getZone().toTimeZone().getRawOffset() / 60000);

            return xts;
        }

        @Override
        public Long convertToAvro(XMLGregorianCalendar xts) {
            if (xts == null) {
                return null;
            }

            MutableDateTime dateTime = new MutableDateTime();
            try {
                dateTime.setYear(xts.getYear());
                dateTime.setMonthOfYear(xts.getMonth());
                dateTime.setDayOfMonth(xts.getDay());
                dateTime.setHourOfDay(xts.getHour());
                dateTime.setMinuteOfHour(xts.getMinute());
                dateTime.setSecondOfMinute(xts.getSecond());
                dateTime.setMillisOfSecond(xts.getMillisecond());

                DateTimeZone tz = DateTimeZone.forOffsetMillis(xts.getTimezone() * 60000);
                if (tz != null) {
                    dateTime.setZoneRetainFields(tz);
                }

                return dateTime.getMillis();
            } catch (IllegalArgumentException e) {
                throw new ComponentException(e);
            }
        }
    }
}
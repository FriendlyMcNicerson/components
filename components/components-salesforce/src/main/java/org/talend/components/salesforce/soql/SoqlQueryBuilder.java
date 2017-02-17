package org.talend.components.salesforce.soql;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;

/**
 * Builds SOQL query with required and optional fields to initialize. Implements Builder-pattern
 */
public class SoqlQueryBuilder {

    /**
     * {@link java.lang.String} constants which are use to build SQOL query
     */
    private static final String SELECT_STATEMENT = "SELECT";

    private static final String FROM_CLAUSE = "FROM";

    private static final String WHERE_CLAUSE = "WHERE";

    private static final String WITH = "WITH";

    private static final String GROUP_BY = "GROUP BY";

    private static final String ORDER_BY = "ORDER BY";

    private static final String LIMIT = "LIMIT";

    private static final String SPACE_SEPARATOR = " ";

    private static final String COMMA = ",";

    private static final String DOT = ".";

    private static final String OPEN_BRACKET = "(";

    private static final String CLOSE_BRACKET = ")";

    private static final String UNDERSCORE = "_";

    private static final String CUSTOM_FIELD_SUFFIX = "__c";

    private static final String RECORDS = "records";

    private static final int SKIP_SUBENTITY_AND_RECORDS_INDEX = 2;

    /**
     * Required field {@link org.apache.avro.Schema} schema
     */
    private final Schema schema;

    /**
     * Required field {@link java.lang.String} entityName
     */
    private final String entityName;

    /**
     * Optional field {@link java.lang.String} whereClauseConditionExpressionString which is used in case of WHERE part
     * is needed
     */
    private final String whereClauseConditionExpressionString;

    /**
     * Optional field {@link java.lang.String} withFilteringExpressionString which is used in case of WITH part is
     * needed
     */
    private final String withFilteringExpressionString;

    /**
     * Optional field {@link java.lang.String} grouping which is used in case of GROUP BY part is needed
     */
    private final String grouping;

    /**
     * Optional field {@link java.lang.String} ordering which is used in case of ORDER BY part is needed
     */
    private final String ordering;

    /**
     * Optional field {@link java.lang.String} numberOfRowsToReturn which is used in case of LIMIT part is needed
     */
    private final int numberOfRowsToReturn;

    /**
     * Static inner class for Builder pattern implementation
     */
    public static class Builder {

        /**
         * Required fields
         */
        private final Schema schema;

        private final String entityName;

        /**
         * Optional fields
         */
        private String whereClauseConditionExpressionString;

        private String withFilteringExpressionString;

        private String grouping;

        private String ordering;

        private int numberOfRowsToReturn;

        /**
         * This constructor is used for required fields initialization.
         *
         * @param schema {@link org.apache.avro.Schema} first parameter
         * @param entityName {@link java.lang.String} second parameter
         */
        public Builder(Schema schema, String entityName) {
            this.schema = schema;
            this.entityName = entityName;
        }

        public Builder whereClauseConditionExpressionString(String value) {
            whereClauseConditionExpressionString = value;
            return this;
        }

        public Builder withFilteringExpressionString(String value) {
            withFilteringExpressionString = value;
            return this;
        }

        public Builder ordering(String value) {
            ordering = value;
            return this;
        }

        public Builder grouping(String value) {
            grouping = value;
            return this;
        }

        public Builder numberOfRowsToReturn(int value) {
            numberOfRowsToReturn = value;
            return this;
        }

        public SoqlQueryBuilder build() {
            return new SoqlQueryBuilder(this);
        }
    }

    private SoqlQueryBuilder(Builder builder) {
        schema = builder.schema;
        entityName = builder.entityName;
        whereClauseConditionExpressionString = builder.whereClauseConditionExpressionString;
        withFilteringExpressionString = builder.withFilteringExpressionString;
        grouping = builder.grouping;
        ordering = builder.ordering;
        numberOfRowsToReturn = builder.numberOfRowsToReturn;
    }

    /**
     * This method is used to build SOQL query.
     *
     * @return {@link java.lang.String} This returns SOQL query.
     */
    public String buildSoqlQuery() {
        StringBuilder resultQuery = new StringBuilder();
        List<String> complexFields = new ArrayList<>();
        resultQuery.append("\"").append(SELECT_STATEMENT).append(SPACE_SEPARATOR);

        for (Schema.Field item : schema.getFields()) {
            if (item.name().contains(CUSTOM_FIELD_SUFFIX)) {
                resultQuery.append(item.name()).append(COMMA).append(SPACE_SEPARATOR);
            } else if (item.name().contains(UNDERSCORE) && item.name().contains(RECORDS)) {
                complexFields.add(item.name());
            } else if (item.name().contains(UNDERSCORE) && !item.name().contains(RECORDS)
                    && !item.name().contains(CUSTOM_FIELD_SUFFIX)) {
                resultQuery.append(item.name().replace('_', '.')).append(COMMA).append(SPACE_SEPARATOR);
            } else {
                resultQuery.append(item.name()).append(COMMA).append(SPACE_SEPARATOR);
            }
        }

        resultQuery.delete(resultQuery.length() - 2, resultQuery.length());
        if (!complexFields.isEmpty()) {
            resultQuery.append(COMMA).append(SPACE_SEPARATOR).append(buildSubquery(complexFields));
        }

        resultQuery.append(SPACE_SEPARATOR).append(FROM_CLAUSE).append(SPACE_SEPARATOR).append(entityName);

        if (whereClauseConditionExpressionString != null && !whereClauseConditionExpressionString.isEmpty()) {
            resultQuery.append(SPACE_SEPARATOR).append(WHERE_CLAUSE).append(SPACE_SEPARATOR)
                    .append(whereClauseConditionExpressionString);
        }

        if (withFilteringExpressionString != null && !withFilteringExpressionString.isEmpty()) {
            resultQuery.append(SPACE_SEPARATOR).append(WITH).append(SPACE_SEPARATOR).append(withFilteringExpressionString);
        }

        if (grouping != null && !grouping.isEmpty()) {
            resultQuery.append(SPACE_SEPARATOR).append(GROUP_BY).append(SPACE_SEPARATOR).append(grouping);
        }

        if (ordering != null && !ordering.isEmpty()) {
            resultQuery.append(SPACE_SEPARATOR).append(ORDER_BY).append(SPACE_SEPARATOR).append(ordering);
        }

        if (numberOfRowsToReturn > 0) {
            resultQuery.append(SPACE_SEPARATOR).append(LIMIT).append(SPACE_SEPARATOR).append(numberOfRowsToReturn);
        }

        return resultQuery.append("\"").toString();
    }

    private String buildSubquery(List<String> inputStrings) {
        StringBuilder builder = new StringBuilder();

        builder.append(OPEN_BRACKET).append(SELECT_STATEMENT).append(SPACE_SEPARATOR);

        for (String item : inputStrings) {
            String[] array = item.split(UNDERSCORE);
            if (array.length == 3) {
                builder.append(array[SKIP_SUBENTITY_AND_RECORDS_INDEX]).append(COMMA).append(SPACE_SEPARATOR);
            } else {
                for (int i = SKIP_SUBENTITY_AND_RECORDS_INDEX; i < array.length; i++) {
                    builder.append(array[i]).append(DOT);
                }
                builder.delete(builder.length() - 1, builder.length());
                builder.append(COMMA).append(SPACE_SEPARATOR);
            }
        }
        builder.delete(builder.length() - 2, builder.length());

        builder.append(SPACE_SEPARATOR).append(FROM_CLAUSE).append(SPACE_SEPARATOR)
                .append(inputStrings.get(0).split(UNDERSCORE)[0]).append(CLOSE_BRACKET);

        return builder.toString();
    }
}

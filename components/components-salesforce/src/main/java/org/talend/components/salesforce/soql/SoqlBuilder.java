package org.talend.components.salesforce.soql;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;

/**
 * Created by pavlo.fandych on 2/13/2017.
 */
public class SoqlBuilder {

    private static final String SELECT_STATEMENT = "SELECT";

    private static final String FROM_CLAUSE = "FROM";

    private static final String SPACE_SEPARATOR = " ";

    private static final String COMMA = ",";

    private static final String DOT = ".";

    private static final String OPEN_BRACKET = "(";

    private static final String CLOSE_BRACKET = ")";

    private static final String UNDERSCORE = "_";

    private static final String RECORDS = "records";

    private static final int SKIP_SUBENTITY_AND_RECORDS_INDEX = 2;

    private static SoqlBuilder soqlBuilderRef;

    private SoqlBuilder() {
    }

    public static SoqlBuilder getInstance() {
        if (SoqlBuilder.soqlBuilderRef == null) {
            SoqlBuilder.soqlBuilderRef = new SoqlBuilder();
        }
        return SoqlBuilder.soqlBuilderRef;
    }

    public String buildQuery(Schema schema, String entityName) {
        StringBuilder resultQuery = new StringBuilder();
        List<String> complexFields = new ArrayList<>();
        resultQuery.append("\"").append(SELECT_STATEMENT).append(SPACE_SEPARATOR);

        for (Schema.Field item : schema.getFields()) {
            if (item.name().contains(UNDERSCORE) && !item.name().contains(RECORDS)) {
                resultQuery.append(item.name().replace('_', '.')).append(COMMA).append(SPACE_SEPARATOR);
            } else if (item.name().contains(UNDERSCORE) && item.name().contains(RECORDS)) {
                complexFields.add(item.name());
            } else {
                resultQuery.append(item.name()).append(COMMA).append(SPACE_SEPARATOR);
            }
        }
        resultQuery.delete(resultQuery.length() - 2, resultQuery.length());
        if (!complexFields.isEmpty()) {
            resultQuery.append(COMMA).append(SPACE_SEPARATOR).append(buildSubquery(complexFields));
        }

        resultQuery.append(SPACE_SEPARATOR).append(FROM_CLAUSE).append(SPACE_SEPARATOR).append(entityName).append("\"");
        return resultQuery.toString();
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

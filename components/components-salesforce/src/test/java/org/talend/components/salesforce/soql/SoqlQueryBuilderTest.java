package org.talend.components.salesforce.soql;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit-tests for {@link SoqlQueryBuilder} class
 */
public class SoqlQueryBuilderTest {

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     */
    @Test
    public void simpleSoqlQueryTest() {
        String expected = "\"SELECT Id, Name, BillingCity FROM Account\"";

        Schema schema = SchemaBuilder.record("Result").fields().name("Id").type().stringType().noDefault().name("Name").type()
                .stringType().noDefault().name("BillingCity").type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Account").build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     * with child-to-parent relationship
     */
    @Test
    public void childToParentTest() {
        String expected = "\"SELECT Name, Account.Name, Account.Owner.Name FROM Contact\"";

        Schema schema = SchemaBuilder.record("Result").fields().name("Name").type().stringType().noDefault().name("Account_Name")
                .type().stringType().noDefault().name("Account_Owner_Name").type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Contact").build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     * with parent-to-child relationship
     */
    @Test
    public void parentToChildTest() {
        String expected = "\"SELECT Name, (SELECT LastName FROM Contacts) FROM Account\"";

        Schema schema = SchemaBuilder.record("Result").fields().name("Name").type().stringType().noDefault()
                .name("Contacts_records_LastName").type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Account").build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     * with parent-to-child relationship in case of three-level entities linking
     */
    @Test
    public void parentToChildDepthTest() {
        String expected = "\"SELECT Name, (SELECT LastName, Account.Owner.Name FROM Contacts) FROM Account\"";

        Schema schema = SchemaBuilder.record("Result").fields().name("Name").type().stringType().noDefault()
                .name("Contacts_records_LastName").type().stringType().noDefault().name("Contacts_records_Account_Owner_Name")
                .type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Account").build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     * with complex relationship
     */
    @Test
    public void complexRelationshipTest() {
        String expected = "\"SELECT Id, Name, (SELECT Quantity, ListPrice, PricebookEntry.UnitPrice, PricebookEntry.Name FROM OpportunityLineItems) FROM Opportunity\"";

        Schema schema = SchemaBuilder.record("Result").fields().name("Id").type().stringType().noDefault().name("Name").type()
                .stringType().noDefault().name("OpportunityLineItems_records_Quantity").type().stringType().noDefault()
                .name("OpportunityLineItems_records_ListPrice").type().stringType().noDefault()
                .name("OpportunityLineItems_records_PricebookEntry_UnitPrice").type().stringType().noDefault()
                .name("OpportunityLineItems_records_PricebookEntry_Name").type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Opportunity").build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     * with custom field
     */
    @Test
    public void customFieldTest() {
        String expected = "\"SELECT Id, SLAExpirationDate__c FROM Account\"";

        Schema schema = SchemaBuilder.record("Result").fields().name("Id").type().stringType().noDefault()
                .name("SLAExpirationDate__c").type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Account").build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     * with WHERE expression
     */
    @Test
    public void whereClauseTest() {
        String expected = "\"SELECT Id, SLAExpirationDate__c FROM Account WHERE Id = 1\"";
        String whereClause = "Id = 1";

        Schema schema = SchemaBuilder.record("Result").fields().name("Id").type().stringType().noDefault()
                .name("SLAExpirationDate__c").type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Account")
                .whereClauseConditionExpressionString(whereClause).build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     * with WITH expression
     */
    @Test
    public void withClauseTest() {
        String expected = "\"SELECT Id, SLAExpirationDate__c FROM Account WITH some_expression\"";
        String withFilteringExpression = "some_expression";

        Schema schema = SchemaBuilder.record("Result").fields().name("Id").type().stringType().noDefault()
                .name("SLAExpirationDate__c").type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Account")
                .withFilteringExpressionString(withFilteringExpression).build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     * with GROUP BY expression
     */
    @Test
    public void groupingSoqlQueryTest() {
        String expected = "\"SELECT Id, SLAExpirationDate__c FROM Account GROUP BY some_expression\"";
        String grouping = "some_expression";

        Schema schema = SchemaBuilder.record("Result").fields().name("Id").type().stringType().noDefault()
                .name("SLAExpirationDate__c").type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Account").grouping(grouping).build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     * with ORDER BY expression
     */
    @Test
    public void orderingSoqlQueryTest() {
        String expected = "\"SELECT Id, SLAExpirationDate__c FROM Account ORDER BY some_expression\"";
        String ordering = "some_expression";

        Schema schema = SchemaBuilder.record("Result").fields().name("Id").type().stringType().noDefault()
                .name("SLAExpirationDate__c").type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Account").ordering(ordering).build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }

    /**
     * Checks {@link SoqlQueryBuilder#buildSoqlQuery()} returns SOQL query according to schema and entity name
     * with LIMIT of returned records
     */
    @Test
    public void limitSoqlQueryTest() {
        String expected = "\"SELECT Id, SLAExpirationDate__c FROM Account ORDER BY some_expression LIMIT 1\"";
        String ordering = "some_expression";

        Schema schema = SchemaBuilder.record("Result").fields().name("Id").type().stringType().noDefault()
                .name("SLAExpirationDate__c").type().stringType().noDefault().endRecord();

        String queryFromBuilder = new SoqlQueryBuilder.Builder(schema, "Account").ordering(ordering).numberOfRowsToReturn(1)
                .build().buildSoqlQuery();

        Assert.assertEquals(expected, queryFromBuilder);
    }
}

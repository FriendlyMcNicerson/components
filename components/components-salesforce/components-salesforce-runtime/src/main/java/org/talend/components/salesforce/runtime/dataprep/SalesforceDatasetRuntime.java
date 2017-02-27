// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.salesforce.runtime.dataprep;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.ReaderDataProvider;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.common.dataset.runtime.DatasetRuntime;
import org.talend.components.salesforce.SalesforceConnectionProperties;
import org.talend.components.salesforce.dataset.SalesforceDatasetProperties;
import org.talend.components.salesforce.datastore.SalesforceDatastoreProperties;
import org.talend.components.salesforce.runtime.SalesforceInputReader;
import org.talend.components.salesforce.runtime.SalesforceSource;
import org.talend.components.salesforce.runtime.SalesforceSourceOrSink;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.java8.Consumer;
import org.talend.daikon.properties.ValidationResult;

/**
 * the data set runtime for salesforce
 *
 */
public class SalesforceDatasetRuntime implements DatasetRuntime<SalesforceDatasetProperties> {

    /**
     * 
     */
    private static final long serialVersionUID = 5829335010543623248L;

    private SalesforceDatasetProperties dataset;

    private RuntimeContainer container;

    @Override
    public ValidationResult initialize(RuntimeContainer container, SalesforceDatasetProperties properties) {
        this.dataset = properties;
        this.container = container;
        return ValidationResult.OK;
    }

    @Override
    public Schema getSchema() {
        SalesforceSourceOrSink sss = new SalesforceSourceOrSink();

        // create a SalesforceConnectionProperties as value passer model only, no other usage
        SalesforceConnectionProperties componentProperties = new SalesforceConnectionProperties("model");
        SalesforceDatastoreProperties datastore = dataset.getDatastoreProperties();

        componentProperties.bulkConnection.setValue(true);
        componentProperties.userPassword.userId.setValue(datastore.userId.getValue());
        componentProperties.userPassword.password.setValue(datastore.password.getValue());
        componentProperties.userPassword.securityKey.setValue(datastore.securityKey.getValue());

        sss.initialize(container, componentProperties);

        try {
            // the UI will be a radio, need to adjust here
            if (dataset.moduleName != null) {
                return sss.getEndpointSchema(container, dataset.moduleName.getValue());
            } else {
                return sss.guessSchema(dataset.query.getValue());
            }
        } catch (IOException e) {
            throw new ComponentException(e);
        }
    }

    @Override
    public void getSample(int limit, Consumer<IndexedRecord> consumer) {
        SalesforceSource ss = new SalesforceSource();

        // create a SalesforceInputProperties as value passer model only, no other usage
        TSalesforceInputProperties componentProperties = new TSalesforceInputProperties("model");
        SalesforceDatastoreProperties datastore = dataset.getDatastoreProperties();

        componentProperties.connection.bulkConnection.setValue(true);
        componentProperties.queryMode.setValue(TSalesforceInputProperties.QueryMode.Bulk);

        componentProperties.connection.userPassword.userId.setValue(datastore.userId.getValue());
        componentProperties.connection.userPassword.password.setValue(datastore.password.getValue());
        componentProperties.connection.userPassword.securityKey.setValue(datastore.securityKey.getValue());

        componentProperties.module.moduleName.setValue(dataset.moduleName.getValue());
        componentProperties.query.setValue(dataset.query.getValue());

        throwExceptionIfValidationResultIsError(ss.initialize(container, componentProperties));
        throwExceptionIfValidationResultIsError(ss.validate(container));

        SalesforceInputReader reader = (SalesforceInputReader) ss.createReader(container);
        ReaderDataProvider<IndexedRecord> readerDataProvider = new ReaderDataProvider<>(reader, limit, consumer);
        readerDataProvider.retrieveData();
    }

    private void throwExceptionIfValidationResultIsError(ValidationResult validationResult) {
        if (validationResult == null) {
            return;
        }

        if (validationResult.getStatus() == ValidationResult.Result.ERROR) {
            throw TalendRuntimeException.createUnexpectedException(validationResult.getMessage());
        }
    }

}

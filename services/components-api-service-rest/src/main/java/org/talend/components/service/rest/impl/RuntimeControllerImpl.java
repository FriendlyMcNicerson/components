//==============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
//==============================================================================

package org.talend.components.service.rest.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.components.common.dataset.DatasetDefinition;
import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.common.dataset.runtime.DatasetRuntime;
import org.talend.components.common.datastore.DatastoreDefinition;
import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.components.common.datastore.runtime.DatastoreRuntime;
import org.talend.components.service.rest.RuntimesController;
import org.talend.components.service.rest.dto.DatasetConnectionInfo;
import org.talend.components.service.rest.dto.ValidationResultsDto;
import org.talend.components.service.rest.serialization.JsonSerializationHelper;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.daikon.definition.service.DefinitionRegistryService;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.runtime.RuntimeUtil;
import org.talend.daikon.sandbox.SandboxedInstance;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.MAX_VALUE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.toInputStream;
import static org.apache.commons.lang3.Validate.notNull;

@ServiceImplementation
@SuppressWarnings("unchecked")
public class RuntimeControllerImpl implements RuntimesController {

    private static final Logger log = LoggerFactory.getLogger(RuntimeControllerImpl.class);

    @Autowired
    private JsonSerializationHelper jsonSerializationHelper;

    @Autowired
    private DefinitionRegistryService definitionServiceDelegate;

    @Override
    public ResponseEntity<ValidationResultsDto> validateDataStoreConnection(String dataStoreDefinitionName,
                                                                            InputStream formData) {
        final DatastoreDefinition<DatastoreProperties> definition = definitionServiceDelegate.getDefinitionsMapByType(
                DatastoreDefinition.class).get(dataStoreDefinitionName);
        notNull(definition, "Could not find data store definition of name %s", dataStoreDefinitionName);
        DatastoreProperties properties = (DatastoreProperties) jsonSerializationHelper.toProperties(formData);

        try (SandboxedInstance instance = RuntimeUtil.createRuntimeClass(definition.getRuntimeInfo(properties),
                getClass().getClassLoader())) {
            DatastoreRuntime datastoreRuntime = (DatastoreRuntime) instance.getInstance();
            datastoreRuntime.initialize(null, properties);
            Iterable<ValidationResult> healthChecks = datastoreRuntime.doHealthChecks(null);

            ValidationResultsDto response = new ValidationResultsDto(newArrayList(healthChecks));
            HttpStatus httpStatus = response.getStatus() == ValidationResult.Result.OK ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

            return new ResponseEntity<>(response, httpStatus);
        }
    }

    @Override
    public String getDatasetSchema(String datasetDefinitionName, DatasetConnectionInfo connectionInfo) throws IOException {
        return useDatasetRuntime(datasetDefinitionName, connectionInfo, runtime -> runtime.getSchema().toString(false));
    }

    @Override
    public StreamingResponseBody getDatasetData(String datasetDefinitionName, DatasetConnectionInfo connectionInfo, Integer from,
                                                Integer limit) {
        return useDatasetRuntime(datasetDefinitionName, connectionInfo, new DatasetContentWriter(limit, true));
    }

    @Override
    public StreamingResponseBody getDatasetDataAsBinary(String datasetDefinitionName, DatasetConnectionInfo connectionInfo,
                                                        Integer from, Integer limit) {
        return useDatasetRuntime(datasetDefinitionName, connectionInfo, new DatasetContentWriter(limit, false));
    }

    private <T> T useDatasetRuntime(String datasetDefinitionName, DatasetConnectionInfo formData,
                                    Function<DatasetRuntime<DatasetProperties<DatastoreProperties>>, T> consumer) {
        // 1) create data store properties from posted data
        DatastoreProperties datastoreProperties = (DatastoreProperties) jsonSerializationHelper.toProperties(
                toInputStream(formData.getDataStoreFormData().toString(), UTF_8));

        // 2) create data set properties from with posted data
        DatasetProperties datasetProperties = (DatasetProperties) jsonSerializationHelper.toProperties(
                toInputStream(formData.getDataSetFormData().toString(), UTF_8));

        // 3) enrich dataset properties with data store properties
        datasetProperties.setDatastoreProperties(datastoreProperties);

        // 4) Retrieve data set definition to be able to create the runtime
        final DatasetDefinition<DatasetProperties<DatastoreProperties>> datasetDefinition = definitionServiceDelegate.getDefinitionsMapByType(
                DatasetDefinition.class).get(datasetDefinitionName);

        // 5) create the runtime
        try (SandboxedInstance instance = RuntimeUtil.createRuntimeClass(
                datasetDefinition.getRuntimeInfo(datasetProperties), getClass().getClassLoader())) {
            DatasetRuntime<DatasetProperties<DatastoreProperties>> datasetRuntimeInstance = (DatasetRuntime<DatasetProperties<DatastoreProperties>>) instance
                    .getInstance();

            datasetRuntimeInstance.initialize(null, datasetProperties);

            // 6) Consume the data set runtime
            return consumer.apply(datasetRuntimeInstance);
        }
    }

    private static class DatasetContentWriter
            implements Function<DatasetRuntime<DatasetProperties<DatastoreProperties>>, StreamingResponseBody> {

        private final Integer limit;

        private final boolean json;

        /**
         * @param limit the number of records to write
         * @param json  true to write JSon, false for binary Avro
         */
        public DatasetContentWriter(Integer limit, boolean json) {
            this.limit = limit;
            this.json = json;
        }

        @Override
        public StreamingResponseBody apply(DatasetRuntime<DatasetProperties<DatastoreProperties>> dr) {
            return output -> writeContentInOutput(dr, output);
        }

        private void writeContentInOutput(DatasetRuntime<DatasetProperties<DatastoreProperties>> dr, OutputStream output) {
            Schema schema = dr.getSchema();
            GenericDatumWriter<IndexedRecord> writer = new GenericDatumWriter<>(schema);
            try {
                Encoder encoder;
                if (json) {
                    encoder = EncoderFactory.get().jsonEncoder(schema, output);
                } else {
                    encoder = EncoderFactory.get().binaryEncoder(output, null);
                }
                dr.getSample(limit == null ? MAX_VALUE : limit, ir -> writeIndexedRecord(writer, encoder, ir));
                encoder.flush();
            } catch (IOException e) {
                log.error("Couldn't create Avro records JSon encoder.", e);
                throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
            }
        }

        private void writeIndexedRecord(GenericDatumWriter<IndexedRecord> writer, Encoder encoder, IndexedRecord indexedRecord) {
            try {
                writer.write(indexedRecord, encoder);
            } catch (IOException e) {
                log.warn("Couldn't serialize Avro record.", e);
            }
        }
    }
}

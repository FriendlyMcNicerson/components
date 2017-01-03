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

package org.talend.components.pubsub.runtime;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
//import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Sample;
import com.google.cloud.Page;
import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.Topic;
import org.talend.components.adapter.beam.coders.LazyAvroCoder;
import org.talend.components.adapter.beam.transform.DirectConsumerCollector;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.common.dataset.runtime.DatasetRuntime;
import org.talend.components.pubsub.PubSubDatasetProperties;
import org.talend.components.pubsub.input.PubSubInputProperties;
import org.talend.daikon.java8.Consumer;
import org.talend.daikon.properties.ValidationResult;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PubSubDatasetRuntime implements IPubSubDatasetRuntime {

    /**
     * The dataset instance that this runtime is configured for.
     */
    private PubSubDatasetProperties properties = null;

    @Override
    public ValidationResult initialize(RuntimeContainer container, PubSubDatasetProperties properties) {
        this.properties = properties;
        return ValidationResult.OK;
    }

    @Override
    public Schema getSchema() {
        //TODO(bchen)
        // Simple schema container.
        final Schema[] s = new Schema[1];
        // Try to get one record and determine its schema in a callback.
        getSample(1, new Consumer<IndexedRecord>() {
            @Override
            public void accept(IndexedRecord in) {
                s[0] = in.getSchema();
            }
        });
        // Return the discovered schema.
        return s[0];
    }

    @Override
    public void getSample(int limit, Consumer<IndexedRecord> consumer) {
        //TODO(bchen) because PubSub do not have offset, and the message will be deleted after
        // read, so have to create a dumy reader which do not call ack to the server, or let the
        // user have a duplicate subscription?

        // Create an input runtime based on the properties.
        PubSubInputRuntime inputRuntime = new PubSubInputRuntime();
        PubSubInputProperties inputProperties = new PubSubInputProperties(null);
        inputProperties.init();
        inputProperties.setDatasetProperties(properties);
        inputRuntime.initialize(null, inputProperties);

        // Create a pipeline using the input component to get records.
        PipelineOptions options = PipelineOptionsFactory.create();
//        options.setRunner(DirectRunner.class);
        final Pipeline p = Pipeline.create(options);
        LazyAvroCoder.registerAsFallback(p);

        try (DirectConsumerCollector<IndexedRecord> collector = DirectConsumerCollector.of(consumer)) {
            // Collect a sample of the input records.
            p.apply(inputRuntime) //
                    .apply(Sample.<IndexedRecord> any(limit)).apply(collector);
            p.run();
        }
    }

    @Override
    public Set<String> listTopics() {
        PubSub pubsub = PubSubConnection.createClient(properties.getDatastoreProperties());
        Page<Topic> topicPage = pubsub.listTopics(PubSub.ListOption.pageSize(100));
        Iterator<Topic> topicIterator = topicPage.iterateAll();
        Set<String> topicsName = new HashSet<>();
        while (topicIterator.hasNext()) {
            topicsName.add(topicIterator.next().getName());
        }
        return topicsName;
    }
}

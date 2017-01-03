package org.talend.components.pubsub.runtime;

import static org.junit.Assert.assertTrue;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDataset;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatastore;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.components.pubsub.PubSubDatastoreProperties;

import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.TopicInfo;

public class PubSubDatasetRuntimeTest {

    final static List<String> topics = Arrays.asList("pbcomponenttopic1", "pbcomponenttopic2", "pbcomponenttopic3");

    static PubSub client = PubSubConnection.createClient(createDatastore());

    PubSubDatasetRuntime runtime;

    @BeforeClass
    public static void initTopics() {
        for (String topic : topics) {
            client.create(TopicInfo.of(topic));
        }
    }

    @AfterClass
    public static void cleanTopics() {
        for (String topic : topics) {
            client.deleteTopic(topic);
        }
    }

    @Before
    public void reset() {
        runtime = new PubSubDatasetRuntime();
    }

    @Test
    public void listTopics() {
        runtime.initialize(null, createDataset(createDatastore()));
        Set<String> retrieveTopics = runtime.listTopics();
        for (String topic : topics) {
            // GCP is a public resource, can't make sure the available topics only for tcomp test
            assertTrue(retrieveTopics.contains(topic));
        }
    }

    @Test
    public void getSchema() {
        // TODO
    }

    @Test
    public void getSample() {
        // TODO
    }

}

package org.talend.components.pubsub.runtime;

import static org.junit.Assert.*;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDataset;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatastore;

import org.apache.beam.sdk.testing.TestPipeline;
import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.SubscriptionInfo;
import com.google.cloud.pubsub.TopicInfo;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.talend.components.pubsub.PubSubDatasetProperties;
import org.talend.components.pubsub.PubSubDatastoreProperties;

import java.util.ArrayList;
import java.util.List;

public class PubSubOutputRuntimeTest {
    final static String topicName = "tcomp-pubsub-outputtest";

    final static String subscriptionName = "tcomp-pubsub-outputtest-sub1";

    static PubSub client = PubSubConnection.createClient(createDatastore());

    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    List<Person> expectedPersons = new ArrayList<>();

    Integer maxRecords = 10;

    PubSubDatastoreProperties datastoreProperties;

    PubSubDatasetProperties datasetProperties;

    @BeforeClass
    public static void initTopic() {
        client.create(TopicInfo.of(topicName));
        client.create(SubscriptionInfo.of(topicName, subscriptionName));
    }

    @AfterClass
    public static void cleanTopic() {
        client.deleteTopic(topicName);
        client.deleteSubscription(subscriptionName);
    }

    @Before
    public void init() {
        datastoreProperties = createDatastore();
        datasetProperties = createDataset(datastoreProperties);
    }
}

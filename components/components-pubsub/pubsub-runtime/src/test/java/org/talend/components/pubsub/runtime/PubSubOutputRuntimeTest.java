package org.talend.components.pubsub.runtime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDataset;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatasetFromCSV;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatastore;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createOutput;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.adapter.beam.coders.LazyAvroCoder;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;
import org.talend.components.pubsub.PubSubDatasetProperties;
import org.talend.components.pubsub.PubSubDatastoreProperties;

import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.ReceivedMessage;
import com.google.cloud.pubsub.SubscriptionInfo;
import com.google.cloud.pubsub.TopicInfo;

public class PubSubOutputRuntimeTest implements Serializable {

    final static String topicName = "tcomp-pubsub-outputtest";

    final static String subscriptionName = "tcomp-pubsub-outputtest-sub1";

    static PubSub client = PubSubConnection.createClient(createDatastore());

    static {
        PubSubAvroRegistry.get();
    }

    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    Integer maxRecords = 10;

    PubSubDatastoreProperties datastoreProperties;

    PubSubDatasetProperties datasetProperties;

    @BeforeClass
    public static void initTopic() {
        client.create(TopicInfo.of(topicName));
        client.create(SubscriptionInfo.of(topicName, subscriptionName));
    }

    @AfterClass
    public static void cleanTopic() throws Exception {
        client.deleteTopic(topicName);
        client.deleteSubscription(subscriptionName);
        client.close();
    }

    @Before
    public void init() {
        datastoreProperties = createDatastore();
        datasetProperties = createDataset(datastoreProperties, topicName);
    }

    @Test
    public void outputCsv() throws UnsupportedEncodingException {
        String testID = "csvBasicTest" + new Random().nextInt();
        final String fieldDelimited = ";";

        List<Person> expectedPersons = Person.genRandomList(testID, maxRecords);
        List<String> expectedMessages = new ArrayList<>();
        List<String[]> sendMessages = new ArrayList<>();
        for (Person person : expectedPersons) {
            expectedMessages.add(person.toCSV(fieldDelimited));
            sendMessages.add(person.toCSV(fieldDelimited).split(fieldDelimited));
        }

        PubSubOutputRuntime outputRuntime = new PubSubOutputRuntime();
        outputRuntime.initialize(null, createOutput(createDatasetFromCSV(createDatastore(), topicName, fieldDelimited)));

        PCollection<IndexedRecord> records = (PCollection<IndexedRecord>) pipeline.apply(Create.of(sendMessages))
                .apply((PTransform) ConvertToIndexedRecord.of());

        records.setCoder(LazyAvroCoder.of()).apply(outputRuntime);

        pipeline.run().waitUntilFinish();

        List<String> actual = new ArrayList<>();
        while (true) {
            Iterator<ReceivedMessage> messageIterator = client.pull(subscriptionName, maxRecords);
            while (messageIterator.hasNext()) {
                ReceivedMessage next = messageIterator.next();
                actual.add(next.getPayloadAsString());
                next.ack();
            }
            if (actual.size() >= maxRecords) {
                break;
            }
        }
        assertThat(actual, containsInAnyOrder(expectedMessages.toArray()));

    }

}

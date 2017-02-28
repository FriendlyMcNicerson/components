package org.talend.components.pubsub.runtime;

import static org.talend.components.pubsub.runtime.PubSubTestConstants.addSubscriptionForDataset;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDataset;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatasetFromAvro;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatasetFromCSV;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatastore;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;
import org.talend.components.pubsub.PubSubDatasetProperties;
import org.talend.components.pubsub.PubSubDatastoreProperties;

import com.google.cloud.ByteArray;
import com.google.cloud.pubsub.Message;
import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.SubscriptionInfo;
import com.google.cloud.pubsub.TopicInfo;

public class PubSubInputRuntimeTest {

    final static String topicName = "tcomp-pubsub-inputtest";

    final static String subscriptionName = "tcomp-pubsub-inputtest-sub1";

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
    public void inputCsv() {
        String testID = "csvBasicTest" + new Random().nextInt();
        final String fieldDelimited = ";";

        expectedPersons = Person.genRandomList(testID, maxRecords);
        List<Message> messages = new ArrayList<>();
        for (Person person : expectedPersons) {
            messages.add(Message.of(person.toCSV(fieldDelimited)));
        }
        client.publish(topicName, messages);

        PubSubInputRuntime inputRuntime = new PubSubInputRuntime();
        inputRuntime.initialize(null, createInput(
                addSubscriptionForDataset(createDatasetFromCSV(createDatastore(), topicName, fieldDelimited), subscriptionName),
                null, maxRecords));

        PCollection<IndexedRecord> readMessages = pipeline.apply(inputRuntime);

        List<IndexedRecord> expected = new ArrayList<>();
        for (Person person : expectedPersons) {
            expected.add(ConvertToIndexedRecord.convertToAvro(person.toCSV(fieldDelimited).split(fieldDelimited)));
        }
        PAssert.that(readMessages).containsInAnyOrder(expected);

        pipeline.run().waitUntilFinish();

    }

    @Test
    public void inputAvro() throws IOException {
        String testID = "avroBasicTest" + new Random().nextInt();

        expectedPersons = Person.genRandomList(testID, maxRecords);
        List<Message> messages = new ArrayList<>();
        for (Person person : expectedPersons) {
            messages.add(Message.of(ByteArray.copyFrom(person.serToAvroBytes())));
        }
        client.publish(topicName, messages);

        PubSubInputRuntime inputRuntime = new PubSubInputRuntime();
        inputRuntime
                .initialize(null,
                        createInput(addSubscriptionForDataset(
                                createDatasetFromAvro(createDatastore(), topicName, Person.schema.toString()), subscriptionName),
                                null, maxRecords));

        PCollection<IndexedRecord> readMessages = pipeline.apply(inputRuntime);

        List<IndexedRecord> expected = new ArrayList<>();
        for (Person person : expectedPersons) {
            expected.add(person.toAvroRecord());
        }
        PAssert.that(readMessages).containsInAnyOrder(expected);

        pipeline.run().waitUntilFinish();
    }
}

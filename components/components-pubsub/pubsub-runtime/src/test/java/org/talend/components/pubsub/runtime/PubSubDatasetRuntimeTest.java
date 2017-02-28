package org.talend.components.pubsub.runtime;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDataset;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatastore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.avro.Schema;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.cloud.pubsub.Message;
import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.SubscriptionInfo;
import com.google.cloud.pubsub.TopicInfo;

public class PubSubDatasetRuntimeTest {

    final static List<String> topics = Arrays.asList("tcomp-pubsub-datasettest1", "tcomp-pubsub-datasettest2",
            "tcomp-pubsub-datasettest3");

    final static List<String> subscriptionsForTP1 = Arrays.asList("tcomp-pubsub-datasettest1-sub1",
            "tcomp-pubsub-datasettest1-sub2", "tcomp-pubsub-datasettest1-sub3");

    final static String subForTP2 = "tcomp-pubsub-datasettest2-sub";

    final static String subForTP3 = "tcomp-pubsub-datasettest3-sub";

    static PubSub client = PubSubConnection.createClient(createDatastore());

    PubSubDatasetRuntime runtime;

    final static String fieldDelimited = ";";

    @BeforeClass
    public static void initTopics() {
        for (String topic : topics) {
            client.create(TopicInfo.of(topic));
        }
        for (String sub : subscriptionsForTP1) {
            client.create(SubscriptionInfo.of(topics.get(0), sub));
        }
        client.create(SubscriptionInfo.of(topics.get(1), subForTP2));
        client.create(SubscriptionInfo.of(topics.get(2), subForTP3));

        //send csv format to topic 2
        Integer maxRecords = 10;
        String testID = "csvBasicTest" + new Random().nextInt();
        List<Person> expectedPersons = Person.genRandomList(testID, maxRecords);
        List<Message> messages = new ArrayList<>();
        for (Person person : expectedPersons) {
            messages.add(Message.of(person.toCSV(fieldDelimited)));
        }
        client.publish(topics.get(1), messages);

        //send avro format to topic 3
    }

    @AfterClass
    public static void cleanTopics() throws Exception {
        for (String topic : topics) {
            client.deleteTopic(topic);
        }
        for (String sub : subscriptionsForTP1) {
            client.deleteSubscription(sub);
        }
        client.deleteSubscription(subForTP2);
        client.deleteSubscription(subForTP3);
        client.close();
    }

    @Before
    public void reset() {
        runtime = new PubSubDatasetRuntime();
    }

    @Test
    public void listTopics() throws Exception {
        runtime.initialize(null, createDataset(createDatastore(), null));
        Set<String> retrieveTopics = runtime.listTopics();
        for (String topic : topics) {
            // GCP is a public resource, can't make sure the available topics only for tcomp test
            assertTrue(retrieveTopics.contains(topic));
        }
    }

    @Test
    public void listSubscriptions() throws Exception {
        runtime.initialize(null, createDataset(createDatastore(), topics.get(0)));
        Set<String> retrieveSubs = runtime.listSubscriptions();
        assertThat(retrieveSubs, containsInAnyOrder(subscriptionsForTP1.toArray()));
    }

    @Test
    public void getSchema() {
        runtime.initialize(null, createDataset(createDatastore(), topics.get(1)));
        Schema schema = runtime.getSchema();
        System.out.println(schema);
    }

    @Test
    public void getSample() {
        // TODO
    }

}

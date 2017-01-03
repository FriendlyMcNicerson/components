package org.talend.components.pubsub.runtime;

import static org.junit.Assert.assertEquals;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatastore;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.properties.ValidationResult;

public class PubSubDatastoreRuntimeTest {

    PubSubDatastoreRuntime runtime;

    @Before
    public void reset() {
        runtime = new PubSubDatastoreRuntime();
    }

    @Test
    public void doHealthChecksTest() {
        runtime.initialize(null, createDatastore());
        Iterable<ValidationResult> validationResults = runtime.doHealthChecks(null);
        assertEquals(ValidationResult.OK, validationResults.iterator().next());
    }

    // @Test
    // public void testAuth() {
    // // Instantiates a client
    // PubSub pubsub = PubSubOptions.getDefaultInstance().getService();
    //
    // // The name for the new topic
    // String topicName = "my-new-topic";
    //
    // // Creates the new topic
    // Topic topic = pubsub.create(TopicInfo.of(topicName));
    //
    // System.out.printf("Topic %s created.%n", topic.getName());
    // }
    //
    // @Test
    // public void createSubscriber() {
    // PubSub pubsub = PubSubOptions.getDefaultInstance().getService();
    // SubscriptionInfo subscriber = SubscriptionInfo.of("pstest", "sub1");
    // Subscription subscription = pubsub.create(subscriber);
    // System.out.println(subscription.toString());
    // }
    //
    // @Test
    // public void sendMessage() {
    // PubSub pubsub = PubSubOptions.getDefaultInstance().getService();
    // List<Message> messages = new ArrayList<>();
    // for(int i = 0; i < 10 ; i++){
    // messages.add(Message.of("m" + i));
    // }
    // pubsub.publish("pstest", messages);
    // }
    //
    // @Test
    // public void getOldMessage() {
    // PubSub pubsub = PubSubOptions.getDefaultInstance().getService();
    // Iterator<ReceivedMessage> messages = pubsub.pull("sub1", 10);
    // while(messages.hasNext()) {
    // System.out.println(messages.next().toString());
    // }
    // }
}

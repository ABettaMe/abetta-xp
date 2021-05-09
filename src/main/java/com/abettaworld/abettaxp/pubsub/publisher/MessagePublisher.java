package com.abettaworld.abettaxp.pubsub.publisher;

public interface MessagePublisher {

    void publish(Object message);

    String getTopic();
}

package com.abettaworld.abettaxp.pubsub.publisher;

import com.abettaworld.abettaxp.pubsub.RedisChannelTopics;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DummyMessagePublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public DummyMessagePublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(Object message) {
        redisTemplate.convertAndSend(this.getTopic(), message);
    }

    @Override
    public String getTopic() {
        return RedisChannelTopics.recTopic.getTopic();
    }
}

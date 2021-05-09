package com.abettaworld.abettaxp.pubsub.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisMsgListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] bytes) {
        log.info("~ RedisMsgListener ~ received message: " + new String(message.getBody()));
    }
}

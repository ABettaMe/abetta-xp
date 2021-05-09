package com.abettaworld.abettaxp.pubsub;

import org.springframework.data.redis.listener.ChannelTopic;

public class RedisChannelTopics {
    public static final ChannelTopic recTopic = new ChannelTopic("abettame:rec:rec-created");
}

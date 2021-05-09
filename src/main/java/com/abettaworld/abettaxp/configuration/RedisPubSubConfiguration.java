package com.abettaworld.abettaxp.configuration;

import com.abettaworld.abettaxp.pubsub.RedisChannelTopics;
import com.abettaworld.abettaxp.pubsub.listener.RedisMsgListener;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "com.abettaworld.redis")
public class RedisPubSubConfiguration {

    private String host;
    private Integer port;
    private String password;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        if (isNotEmpty(password)) {
            config.setPassword(password);
        }
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }

    @Bean
    RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        container.addMessageListener(
                new MessageListenerAdapter(new RedisMsgListener()),
                RedisChannelTopics.recTopic
        );
        return container;
    }
}

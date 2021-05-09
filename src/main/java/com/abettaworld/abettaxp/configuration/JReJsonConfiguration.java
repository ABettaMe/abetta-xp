package com.abettaworld.abettaxp.configuration;

import com.redislabs.modules.rejson.JReJSON;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "com.abettaworld.redis")
public class JReJsonConfiguration {

    private String host;
    private Integer port;
    private String password;

    @Bean
    public JReJSON reJsonClient() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        JedisPool jedisPool = isNotEmpty(password) ?
                new JedisPool(poolConfig, host, port, 30000, password) : new JedisPool(host, port);
        return new JReJSON(jedisPool);
    }
}

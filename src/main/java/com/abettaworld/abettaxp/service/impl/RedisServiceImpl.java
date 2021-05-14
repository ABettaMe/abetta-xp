package com.abettaworld.abettaxp.service.impl;

import com.abettaworld.abettaxp.service.RedisService;
import com.redislabs.modules.rejson.JReJSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    private final JReJSON reJsonClient;

    public RedisServiceImpl(JReJSON reJsonClient) {
        this.reJsonClient = reJsonClient;
    }

    @Override
    public <T> void saveResource(String lookupKey, T resource) {
        reJsonClient.set(lookupKey, resource);
    }

    @Override
    public <T> Optional<T> getResourceByLookupKey(String lookupKey, Class<T> clazz) {
        try {
            T resource = reJsonClient.get(lookupKey, clazz);
            return Optional.of(resource);
        } catch (Exception exception) {
            log.info("Resource doesn't exist?");
            return Optional.empty();
        }
    }

    @Override
    public <T> List<T> getResourcesByLookupKeys(String[] lookupKeys, Class<T> clazz) {
        try {
            return reJsonClient.mget(clazz, lookupKeys);
        } catch (Exception exception) {
            log.info("Resources doesn't exist?");
            return Collections.emptyList();
        }
    }
}

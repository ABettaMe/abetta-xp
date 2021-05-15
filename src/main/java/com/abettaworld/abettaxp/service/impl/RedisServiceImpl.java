package com.abettaworld.abettaxp.service.impl;

import com.abettaworld.abettaxp.service.RedisService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
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
    public void saveResource(String lookupKey, MessageOrBuilder resource) {
        String payload = null;
        try {
            payload = JsonFormat.printer().print(resource);
            reJsonClient.set(lookupKey, payload);
        } catch (InvalidProtocolBufferException e) {
            log.info("Something went wrong when trying to save resource.");
        }
    }

    @Override
    public <T extends Message.Builder> Optional<T> getResourceByLookupKey(String lookupKey, T builder) {
        try {
            String payload = reJsonClient.get(lookupKey);
            JsonFormat.parser().ignoringUnknownFields().merge(payload, builder);
            return Optional.of(builder);
        } catch (Exception exception) {
            log.info("Resource doesn't exist?");
            return Optional.empty();
        }
    }

    @Override
    public List<String> getResourcesByLookupKeys(String[] lookupKeys) {
        try {
            return reJsonClient.mget(String.class, lookupKeys);
        } catch (Exception exception) {
            log.info("Resources doesn't exist?");
            return Collections.emptyList();
        }
    }
}

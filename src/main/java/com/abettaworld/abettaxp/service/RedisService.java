package com.abettaworld.abettaxp.service;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

import java.util.List;
import java.util.Optional;

public interface RedisService {

    void saveResource(String lookupKey, MessageOrBuilder resource);

    <T extends Message.Builder> Optional<T> getResourceByLookupKey(String lookupKey, T builder);

    List<String> getResourcesByLookupKeys(String[] lookupKeys);
}

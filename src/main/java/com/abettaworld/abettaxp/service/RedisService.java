package com.abettaworld.abettaxp.service;

import java.util.List;
import java.util.Optional;

public interface RedisService {

    <T> void saveResource(String lookupKey, T resource);

    <T>  Optional<T> getResourceByLookupKey(String lookupKey, Class<T> clazz);

    <T> List<T> getResourcesByLookupKeys(String[] lookupKeys, Class<T> clazz);
}

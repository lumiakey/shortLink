package com.what2e.service;

public interface RedisService {
    void redisWrite(String key,String Value);
    String findLongLink(String key);
}

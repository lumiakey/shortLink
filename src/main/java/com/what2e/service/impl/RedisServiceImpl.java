package com.what2e.service.impl;

import com.what2e.service.RedisService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisServiceImpl implements RedisService {

    static Jedis jedis = new Jedis("localhost");

    @Override
    public void redisWrite(String key,String Value) {
        jedis.set(key, Value);
    }

    @Override
    public String findLongLink(String key) {
        return jedis.get(key);
    }
}

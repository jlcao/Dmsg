package com.dmsg.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by jlcao on 2016/7/20.
 */
public class CacheManager {
    private static JedisPool pool;

    public CacheManager(RedisPoolBuilder builder) {
        pool = builder.build();
    }

    public Jedis getResource() {
        return pool.getResource();
    }

    public void put(String key, String value) {
        pool.getResource().set(key, value);
    }

    public void put(Collection<Map.Entry<String, String>> collection) throws IOException {
        Pipeline piplined = pool.getResource().pipelined();
        for (Map.Entry<String, String> entry : collection) {
            piplined.set(entry.getKey(), entry.getValue());
        }
        piplined.close();

    }
}

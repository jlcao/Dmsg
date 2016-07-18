package com.dmsg.cache;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Created by jlcao on 2016/7/18.
 */
public class CacheFactory {
    private JedisPool jedisPool;
    private JedisPoolConfig poolConfig;
}

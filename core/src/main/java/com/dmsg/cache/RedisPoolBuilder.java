package com.dmsg.cache;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by jlcao on 2016/7/19.
 */
public class RedisPoolBuilder {
    private String host;
    private int port;
    private JedisPoolConfig config;

    private RedisPoolBuilder() {

    }

    public RedisPoolBuilder(String host, int port) {
        config = new JedisPoolConfig();
        this.host = host;
        this.port = port;
    }

    public static RedisPoolBuilder custom(){
        return new RedisPoolBuilder();
    }

    public RedisPoolBuilder setHost(String host) {
        this.host = host;
        return this;
    }
    public RedisPoolBuilder setPort(int port) {
        this.port = port;
        return this;
    }
    public JedisPool build(){
        JedisPool jedisPool = new JedisPool(config,host, port);
        return jedisPool;
    }
}

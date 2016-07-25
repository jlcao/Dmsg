package com.dmsg.server;

import com.dmsg.cache.CacheManager;
import com.dmsg.cache.RedisPoolBuilder;
import com.dmsg.exception.ServerConfigException;
import com.dmsg.netty.NetSocketServer;
import com.dmsg.netty.initializer.InitializerFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import redis.clients.jedis.JedisPool;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by cjl on 2016/7/15.
 */
public class DmsgServerContext {
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private RedisPoolBuilder redisPoolBuilder;
    private CacheManager cache;

    private NetSocketServer netSocketServer;
    private DmsgServerConfig config;

    public DmsgServerContext() {
        initConfig();
        redisPoolBuilder = new RedisPoolBuilder(config.getCacheHost(), config.getCachePort());
        cache = new CacheManager(redisPoolBuilder);
    }

    private void initConfig() {
        config = new DmsgServerConfig();
    }

    public void builderNetSocketServer() throws ServerConfigException {
        netSocketServer = new NetSocketServer(bossGroup, workerGroup, InitializerFactory.create(config.getProtocol()), config.getPort());

    }

    public void start() {
        netSocketServer.run();
        try {
            saveNode();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    private void saveNode() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        String ip=addr.getHostAddress().toString();//获得本机IP
        addr.getHostName();


    }


}

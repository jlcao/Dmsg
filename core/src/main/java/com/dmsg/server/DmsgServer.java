package com.dmsg.server;

import com.dmsg.cache.RedisPoolBuilder;
import com.dmsg.exception.ServerConfigException;
import com.dmsg.netty.NetSocketServer;
import com.dmsg.netty.initializer.InitializerFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Created by cjl on 2016/7/15.
 */
public class DmsgServer {
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private RedisPoolBuilder redisPoolBuilder;

    private NetSocketServer netSocketServer;
    private DmsgServerConfig config;
    private String protocol;

    public DmsgServer() {
        initConfig();
        redisPoolBuilder = new RedisPoolBuilder(config.getCacheHost(), config.getCachePort());
    }

    private void initConfig() {
        config = new DmsgServerConfig();

    }

    public void builderNetSocketServer() throws ServerConfigException {

        netSocketServer = new NetSocketServer(bossGroup, workerGroup, InitializerFactory.create(config.getProtocol()), config.getPort());
    }

    public void start() {
        netSocketServer.run();
    }




}

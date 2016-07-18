package com.dmsg.server;

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
    private NetSocketServer netSocketServer;
    private DmsgServerConfig config;
    private String protocol;



    public void builderNetSocketServer() throws ServerConfigException {
        netSocketServer = new NetSocketServer(bossGroup, workerGroup, InitializerFactory.create(protocol), config.getPort());
    }

    public void start() {
        netSocketServer.run();
    }




}

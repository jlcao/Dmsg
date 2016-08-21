package com.dmsg.netty;

import com.dmsg.netty.initializer.InitializerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cjl on 2016/6/17.
 */
public class NetSocketServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelInitializer<SocketChannel> channelInitializer;
    private int port = 8080;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public NetSocketServer(EventLoopGroup bossGroup, EventLoopGroup workerGroup, ChannelInitializer<SocketChannel> channelInitializer, int port) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.port = port;
        this.channelInitializer = channelInitializer;
    }

    public void run() {

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer);
            Channel ch = b.bind(port).sync().channel();
            logger.info("web socket server started at port {}", port);
            logger.info("open your browser and navigate to ws://localhost:{}" , port);
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workerGroup= new NioEventLoopGroup();
        int port = 8080;
        new NetSocketServer(bossGroup,workerGroup, InitializerFactory.create("websocket"),port).run();
    }
}

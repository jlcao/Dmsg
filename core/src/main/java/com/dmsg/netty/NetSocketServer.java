package com.dmsg.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by cjl on 2016/6/17.
 */
public class NetSocketServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelInitializer<SocketChannel> channelInitializer;
    private int port = 8080;

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
            System.out.println("web socket server started at port " + port);
            System.out.println("open your browser and navigate to http://localhost:" + port);
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

   /* public static void main(String[] args) {
        int port = 8080;
        new NetSocketServer().run();
    }*/
}

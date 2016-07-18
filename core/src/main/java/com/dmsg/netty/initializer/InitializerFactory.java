package com.dmsg.netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by jlcao on 2016/7/18.
 */
public class InitializerFactory {

    public static ChannelInitializer<SocketChannel> create(String protocol) {
        if ("websocket".equals(protocol)) {
            return new WebSocketServerInitializer();
        } else {
            return null;
        }
    }
}

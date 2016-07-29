package com.dmsg.netty;

import com.dmsg.netty.handler.TextWebSocketFrameHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.URI;

/**
 * Created by cjl on 2016/7/29.
 */
public class NetSocketClient {

    private EventLoopGroup bossGroup;
    private String host;
    private int port;
    private WebSocketClientProtocolHandler clientProtocolHandler;

    public NetSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        clientProtocolHandler = new WebSocketClientProtocolHandler(URI.create(""), WebSocketVersion.V13, "", true, new DefaultHttpHeaders(true), 65536);
    }

    public void connection() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup).
                channel(NioServerSocketChannel.class).
                remoteAddress(host, port).
                option(ChannelOption.TCP_NODELAY,true).
                handler(new LoggingHandler(LogLevel.INFO)).
                handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(clientProtocolHandler);
                        pipeline.addLast(new TextWebSocketFrameHandler());
                    }
                });

    }
}

package com.dmsg.netty;

import com.dmsg.netty.handler.ClientContextHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.URI;

/**
 * Created by cjl on 2016/7/29.
 */
public class NetSocketClient {

    private EventLoopGroup bossGroup;
    private String host;
    private int port;
    private WebSocketClientProtocolHandler clientProtocolHandler;
    private String url;

    public NetSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        url = String.format("ws://%s:%d/websocket", host, port);
        this.bossGroup = new NioEventLoopGroup();
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(URI.create(url), WebSocketVersion.V13, "", true, new DefaultHttpHeaders(true));
        clientProtocolHandler = new WebSocketClientProtocolHandler(handshaker);

    }

    public void connection() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup).
                channel(NioSocketChannel.class).
                remoteAddress(host, port).
                option(ChannelOption.TCP_NODELAY,true).
                handler(new LoggingHandler(LogLevel.INFO)).
                handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(clientProtocolHandler);
                        pipeline.addLast(new IdleStateHandler(10, 120, 0));
                        pipeline.addLast(new ClientContextHandler());
                    }
                });
        ChannelFuture channel = bootstrap.connect().sync();
    }

    public static void main(String args[]) throws InterruptedException {
        NetSocketClient client = new NetSocketClient("localhost", 8080);
        client.connection();

        Thread.sleep(1000000);

    }
}

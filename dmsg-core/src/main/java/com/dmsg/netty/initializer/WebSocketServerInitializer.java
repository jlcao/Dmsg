package com.dmsg.netty.initializer;

import com.dmsg.netty.handler.WebSocketServerHandler;
import com.dmsg.netty.handler.ServerContextHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 服务端 ChannelInitializer
 *
 */
public class WebSocketServerInitializer extends
		ChannelInitializer<SocketChannel> {


	@Override
    public void initChannel(SocketChannel ch) throws Exception {
		 ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(65536));
		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new WebSocketServerHandler());
		pipeline.addLast(new ServerContextHandler());
    }
}

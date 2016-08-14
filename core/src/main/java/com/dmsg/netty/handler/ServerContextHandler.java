package com.dmsg.netty.handler;

import com.dmsg.message.MessageContext;
import com.dmsg.message.MessageExecutor;
import com.dmsg.message.MessageHandler;
import com.dmsg.message.MessageParser;
import com.dmsg.server.DmsgServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

/**
 * Created by cjl on 2016/7/13.
 */
public class ServerContextHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    MessageExecutor executor;
    DmsgServerContext serverContext;
    MessageParser parser;


    public ServerContextHandler(){
        serverContext = DmsgServerContext.getServerContext();
        executor = MessageExecutor.getInstance();
        parser = new MessageParser();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        MessageContext messageContext = new MessageContext(serverContext, ctx, msg.text());
        parser.parse(messageContext);
        executor.execute(new MessageHandler(messageContext));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        MessageContext messageContext = new MessageContext(serverContext, ctx, "{header:{msgType:10},body:{}}");
        parser.parse(messageContext);
        executor.execute(new MessageHandler(messageContext));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded");
        super.handlerAdded(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt.equals(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE)) {
            System.out.println("握手完成");
            System.out.println("鉴权");
            ctx.writeAndFlush(new TextWebSocketFrame("{type:\"AUTH\",username:\"127.0.0.1:8080\",password:\"123\"}"));
        }
    }
}

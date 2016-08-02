package com.dmsg.netty.handler;

import com.dmsg.message.MessageContext;
import com.dmsg.message.MessageExecutor;
import com.dmsg.message.MessageHandler;
import com.dmsg.message.MessageParser;
import com.dmsg.message.vo.ControllerMessage;
import com.dmsg.message.vo.MessageType;
import com.dmsg.message.vo.TextMessage;
import com.dmsg.server.DmsgServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

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
        TextMessage message = new TextMessage(msg.text());
        MessageContext messageContext = new MessageContext(serverContext, ctx, message);
        parser.parse(messageContext);
        executor.execute(new MessageHandler(messageContext));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ControllerMessage controllerMessage = new ControllerMessage(MessageType.CONTROLLER_CLOSE.getCode());
        MessageContext messageContext = new MessageContext(serverContext, ctx, controllerMessage);
        parser.parse(messageContext);
        executor.execute(new MessageHandler(messageContext));
        System.out.println("handlerRemoved");
        super.handlerRemoved(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded");
        super.handlerAdded(ctx);
    }
}

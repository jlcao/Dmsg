package com.dmsg.netty.handler;

import com.dmsg.data.HostDetail;
import com.dmsg.message.*;
import com.dmsg.message.vo.AuthReqMessage;
import com.dmsg.message.vo.AuthResMessage;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.server.DmsgServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cjl on 2016/7/13.
 */
public class ServerContextHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    MessageExecutor executor;
    DmsgServerContext serverContext;
    MessageParser parser;
    MessageSender sender;
    Logger logger = LoggerFactory.getLogger(this.getClass());


    public ServerContextHandler(){
        serverContext = DmsgServerContext.getServerContext();
        executor = MessageExecutor.getInstance();
        parser = new MessageParser();
        sender = serverContext.getSender();
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
        logger.debug("handlerAdded");
        super.handlerAdded(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt.equals(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE)) {
            logger.debug("握手完成");
            logger.debug("鉴权");
            HostDetail local = serverContext.getHostDetail();
            AuthReqMessage b = new AuthReqMessage();
            b.setUsername("host " + local.getIp() + ":" + local.getPort());
            MessageBase messageBase = MessageBase.createAuthReq(b, local);
            sender.send(ctx, messageBase);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info(cause.getMessage());
    }
}

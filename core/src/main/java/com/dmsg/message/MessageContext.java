package com.dmsg.message;

import com.dmsg.message.vo.AuthReqMessage;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.server.DmsgServerContext;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by cjl on 2016/7/12.
 */
public class MessageContext {
    private ChannelHandlerContext channelHandlerContext;
    private DmsgServerContext serverContext;
    private String source;
    private MessageBase message;
    private MessageType messageType;
    private String befrom;

    public MessageContext(DmsgServerContext serverContext, ChannelHandlerContext ctx, String text) {
        this.channelHandlerContext = ctx;
        this.serverContext = serverContext;
        this.source = text;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public MessageBase getMessage() {

        return message;
    }

    public DmsgServerContext getServerContext() {
        return serverContext;
    }

    public void setMessage(MessageBase message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public boolean authentication(AuthReqMessage authMessage) {


        return false;
    }

    public void setBefrom(String befrom) {
        this.befrom = befrom;
    }

    public String getBefrom() {
        return befrom;
    }

    @Override
    public String toString() {
        return "MessageContext{" +
                ", source='" + source + '\'' +
                ", message=" + message +
                ", messageType=" + messageType +
                ", befrom='" + befrom + '\'' +
                '}';
    }
}

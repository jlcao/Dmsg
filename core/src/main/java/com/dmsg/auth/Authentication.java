package com.dmsg.auth;

import com.alibaba.fastjson.JSON;
import com.dmsg.filter.DmsgFilter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.AuthReqMessage;
import com.dmsg.message.vo.AuthResMessage;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.server.DmsgServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Created by jlcao on 2016/7/26.
 */
public class Authentication extends DmsgFilter {
    //需要过滤的类型


    public void doFilter(MessageContext messageContext, FilterChain chain) {
        if (MessageType.AUTH_REQ.equals(messageContext.getMessageType())) {
            AuthReqMessage authMessage = (AuthReqMessage) messageContext.getMessage().getBody();
            String username = authMessage.getUsername();
            if (username.startsWith("host#")) {
                host(messageContext, authMessage, username);
            } else {
                user(messageContext, authMessage);
            }

        }
        chain.doFilter(messageContext);

    }

    private void host(MessageContext messageContext, AuthReqMessage authMessage, String username) {
        String[] splits = username.split("#");
        DmsgServerContext serverContext = messageContext.getServerContext();
        ChannelHandlerContext channel = messageContext.getChannelHandlerContext();
        String hosts = splits[1];
        if ("dmsg".equals(serverContext.getConfig().getServerAuthKey())) {
            serverContext.getRemotHostsChannelManager().addContext(hosts, channel);
            AuthResMessage authResMessage = new AuthResMessage();
            authResMessage.setSucc(true);
            authResMessage.setError(0);
            authResMessage.setUsername("host#" + hosts);
            sendRes(serverContext, channel, authResMessage);
        } else {
            AuthResMessage authResMessage = new AuthResMessage();
            authResMessage.setSucc(false);
            authResMessage.setError(1);
            authResMessage.setUsername("host#" + hosts);
            sendRes(serverContext, channel, authResMessage);
        }
    }

    private void sendRes(DmsgServerContext serverContext, ChannelHandlerContext channel, AuthResMessage authResMessage) {
        MessageBase authReq = MessageBase.createAuthReq(authResMessage, serverContext.getHostDetail());
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(authReq)));
    }

    private void user(MessageContext messageContext, AuthReqMessage authMessage) {
        String username = authMessage.getUsername();
        String password = authMessage.getPassword();
        DmsgServerContext serverContext = messageContext.getServerContext();
        ChannelHandlerContext channel = messageContext.getChannelHandlerContext();
        if ("123".equals(password)) {
            String authkey = System.currentTimeMillis() + "";
            serverContext.getUserChannelManager().addContext(username,authkey, channel);
            AuthResMessage authResMessage = new AuthResMessage();
            authResMessage.setSucc(true);
            authResMessage.setAuthKey(authkey);
            authResMessage.setError(0);
            authResMessage.setUsername(username);
            sendRes(serverContext, channel, authResMessage);
        } else {
            AuthResMessage authResMessage = new AuthResMessage();
            authResMessage.setSucc(false);
            authResMessage.setError(1);
            authResMessage.setUsername(username);
            sendRes(serverContext, channel, authResMessage);
        }
    }

    public void destroy() {

    }

    public void init() {
        this.appendAttentionType(MessageType.AUTH_REQ);
    }


}

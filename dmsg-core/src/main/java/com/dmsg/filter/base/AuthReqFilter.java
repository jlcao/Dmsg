package com.dmsg.filter.base;

import com.alibaba.fastjson.JSON;
import com.dmsg.cache.HostCache;
import com.dmsg.channel.RemoteHostChannelManager;
import com.dmsg.data.HostDetail;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jlcao on 2016/7/26.
 */
public class AuthReqFilter extends DmsgFilter {
    //需要过滤的类型
    Logger logger = LoggerFactory.getLogger(this.getClass());


    public void doFilter(MessageContext messageContext, FilterChain chain) {
        if (MessageType.AUTH_REQ.equals(messageContext.getMessageType())) {
            AuthReqMessage authMessage = (AuthReqMessage) messageContext.getMessage().getBody();
            String username = authMessage.getUsername();
            logger.info("收到鉴权请求：username:{}" + username);
            if (username.indexOf("host")>-1) {
                host(messageContext, authMessage, username);
            } else {
                user(messageContext, authMessage);
            }

        }
        //chain.doFilter(messageContext);

    }

    private void host(MessageContext messageContext, AuthReqMessage authMessage, String username) {
        String[] splits = username.split(" ");
        DmsgServerContext serverContext = messageContext.getServerContext();
        HostCache hostCache = serverContext.getHostCache();
        RemoteHostChannelManager channelManager = serverContext.getRemotHostsChannelManager();
        ChannelHandlerContext channel = messageContext.getChannelHandlerContext();
        String hosts = splits[1];
        AuthResMessage authResMessage = new AuthResMessage(true, 0, "host " + hosts);
        if (authMessage.getPassword().equals(serverContext.getConfig().getServerAuthKey())) {
            HostDetail hostDetail = new HostDetail(messageContext.getMessage().getFrom());
            if (!channelManager.isAvailable(hostDetail.keyFiled())) {
                logger.info("存储来自客户端的上下文连接");
                channelManager.addContext(hostDetail.keyFiled(), messageContext.getChannelHandlerContext());
                hostCache.put(hostDetail);
            } else {
                logger.error("已经存在和该客户端的链接");
                authResMessage.setSucc(false);
                authResMessage.setError(2);
                authResMessage.setMsg("已经存在和该客户端的链接");
                authResMessage.setUsername("host " + hosts);
            }
        } else {
            logger.error("主机鉴权校验失败");
            authResMessage.setSucc(false);
            authResMessage.setError(1);
            authResMessage.setMsg("主机鉴权校验失败");
            authResMessage.setUsername("host " + hosts);
        }
        sendRes(serverContext, channel, authResMessage);
    }

    private void sendRes(DmsgServerContext serverContext, ChannelHandlerContext channel, AuthResMessage authResMessage) {
        MessageBase authReq = MessageBase.createAuthRes(authResMessage, serverContext.getHostDetail());
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

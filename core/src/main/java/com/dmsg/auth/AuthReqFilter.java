package com.dmsg.auth;

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
        chain.doFilter(messageContext);

    }

    private void host(MessageContext messageContext, AuthReqMessage authMessage, String username) {
        String[] splits = username.split(" ");
        DmsgServerContext serverContext = messageContext.getServerContext();
        HostCache hostCache = serverContext.getHostCache();
        RemoteHostChannelManager channelManager = serverContext.getRemotHostsChannelManager();
        ChannelHandlerContext channel = messageContext.getChannelHandlerContext();
        String hosts = splits[1];
        logger.info("处理来自{}节点的鉴权请求:{}", authMessage.getUsername(),messageContext);
        if ("dmsg".equals(serverContext.getConfig().getServerAuthKey())) {
            serverContext.getRemotHostsChannelManager().addContext(hosts, channel);
            AuthResMessage authResMessage = new AuthResMessage();
            authResMessage.setSucc(true);
            authResMessage.setError(0);
            authResMessage.setUsername("host " + hosts);

            HostDetail hostDetail = new HostDetail(messageContext.getMessage().getFrom());
            if (!channelManager.isAvailable(hostDetail.keyFiled())) {
                channelManager.addContext(hostDetail.keyFiled(), messageContext.getChannelHandlerContext());
                hostCache.put(hostDetail);
                logger.info("存储来自客户端的上下文连接");
            } else {
                messageContext.getChannelHandlerContext().close();
            }
            sendRes(serverContext, channel, authResMessage);

        } else {
            AuthResMessage authResMessage = new AuthResMessage();
            authResMessage.setSucc(false);
            authResMessage.setError(1);
            authResMessage.setUsername("host " + hosts);
            sendRes(serverContext, channel, authResMessage);
        }
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

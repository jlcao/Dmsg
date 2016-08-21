package com.dmsg.auth;

import com.alibaba.fastjson.JSON;
import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.filter.DmsgFilter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.message.vo.TextMessage;
import com.dmsg.server.DmsgServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Created by cjl on 2016/8/15.
 */
public class BroadcastReqFilter extends DmsgFilter {
    private DmsgServerContext dmsgServerContext;

    public BroadcastReqFilter() {
        this.appendAttentionType(MessageType.BROADCAST_REQ);
    }

    public void doFilter(MessageContext messageContext, FilterChain chain) {
        if (dmsgServerContext == null) {
            this.dmsgServerContext = messageContext.getServerContext();
        }

        /**
         * 广播逻辑处理
         * if (该用户是否在本地)
         *     发送消息
         *     回执广播
         * else
         *     不处理消息
         *
         */
        MessageBase messageBase = messageContext.getMessage();
        LocalUserChannelManager channelManager = messageContext.getServerContext().getUserChannelManager();

        for (String user : messageBase.getTo().getUsers()) {
            ChannelHandlerContext channelContext = channelManager.getContext(user);
            if (channelManager != null) {
                messageBase.getHeader().setMsgType(MessageType.SEND_TEXT.getVal());
                channelContext.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messageBase)));
                MessageBase res = MessageBase.createBroadcastRes(messageBase.getHeader().getMsgId(), user, dmsgServerContext.getHostDetail());
                messageContext.getChannelHandlerContext().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(res)));
            }


        }





    }

    public void destroy() {

    }

    public void init() {

    }
}

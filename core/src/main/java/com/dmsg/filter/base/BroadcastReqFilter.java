package com.dmsg.filter.base;

import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.data.HostDetail;
import com.dmsg.data.UserDetail;
import com.dmsg.filter.DmsgFilter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.message.MessageSender;
import com.dmsg.message.vo.BroadcastReqMessage;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.message.vo.SourceAddress;
import com.dmsg.server.DmsgServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cjl on 2016/8/15.
 */
public class BroadcastReqFilter extends DmsgFilter {
    private DmsgServerContext dmsgServerContext;
    private MessageSender sender;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public void doFilter(MessageContext messageContext, FilterChain chain) {
        if (dmsgServerContext == null) {
            this.dmsgServerContext = messageContext.getServerContext();
        }
        if (sender == null) {
            this.sender = dmsgServerContext.getSender();

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
        BroadcastReqMessage broadcastReqMessage = (BroadcastReqMessage) messageBase.getBody();
        logger.info("广播消息：user[{}]", broadcastReqMessage.getUserName());
        if (channelManager.isAvailable(broadcastReqMessage.getUserName())) {
            MessageBase sourceMessage = broadcastReqMessage.getMessage();
            //发送消息
            sender.send(channelManager.getContext(broadcastReqMessage.getUserName()), sourceMessage);
            saveUserCache(messageContext.getServerContext(), sourceMessage);
            //回执消息
            MessageBase res = MessageBase.createBroadcastRes(sourceMessage.getHeader().getMsgId(), broadcastReqMessage.getUserName(), messageContext.getServerContext().getHostDetail());
            sender.send(messageContext.getChannelHandlerContext(), res);
        }
        chain.doFilter(messageContext);
    }

    private void saveUserCache(DmsgServerContext serverContext, MessageBase sourceMessage) {
        SourceAddress address = sourceMessage.getFrom();

        if (!serverContext.getUserCache().contains(address.getUser())) {
            UserDetail userDetail = new UserDetail();
            HostDetail hostDetail = serverContext.getHostCache().getHost(address.keyFiled());
            if (hostDetail != null) {
                userDetail.setLastTime(System.currentTimeMillis());
                userDetail.setLoginHost(hostDetail);
                userDetail.setStatus(1);
                userDetail.setUserName(address.getUser());
                serverContext.getUserCache().put(userDetail);
            }
        }
    }

    public void destroy() {

    }

    public void init() {
        appendAttentionType(MessageType.BROADCAST_REQ);
    }
}

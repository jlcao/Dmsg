package com.dmsg.auth;

import com.dmsg.cache.HostCache;
import com.dmsg.channel.RemoteHostChannelManager;
import com.dmsg.data.HostDetail;
import com.dmsg.filter.DmsgFilter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.AuthResMessage;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.message.vo.SourceAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jlcao on 2016/8/21.
 */
public class AuthResFilter extends DmsgFilter {
    Logger logger = LoggerFactory.getLogger(this.getClass());


    public void doFilter(MessageContext messageContext, FilterChain chain) {
        MessageBase messageBase = messageContext.getMessage();
        AuthResMessage authResMessage = (AuthResMessage) messageBase.getBody();
        if (authResMessage.getSucc()) {
            HostCache hostCache = messageContext.getServerContext().getHostCache();
            RemoteHostChannelManager channelManager = messageContext.getServerContext().getRemotHostsChannelManager();
            SourceAddress sourceAddress = messageBase.getFrom();
            HostDetail hostDetail = new HostDetail(sourceAddress);
            logger.info("节点 {} ,连接成功",hostDetail);
            if (!channelManager.isAvailable(hostDetail.keyFiled())) {
                logger.info("存储节点================{}",hostDetail);

                channelManager.addContext(hostDetail.keyFiled(), messageContext.getChannelHandlerContext());
                hostCache.put(hostDetail);
            } else {
                messageContext.getChannelHandlerContext().close();
            }

        } else {
            logger.info("节点 {} ,连接失败 {}", messageBase.getFrom(), authResMessage.getError());
        }
    }

    public void destroy() {

    }

    public void init() {
        this.appendAttentionType(MessageType.AUTH_RES);

    }
}

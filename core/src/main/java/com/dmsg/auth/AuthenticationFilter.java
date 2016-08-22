package com.dmsg.auth;

import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.filter.DmsgFilter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.message.vo.SourceAddress;
import com.dmsg.server.DmsgServerContext;
import io.netty.channel.ChannelId;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by cjl on 2016/8/22.
 */
public class AuthenticationFilter extends DmsgFilter {
    private LocalUserChannelManager channelManager;
    private DmsgServerContext dmsgServerContext;

    public void doFilter(MessageContext messageContext, FilterChain chain) {
        if (dmsgServerContext == null) {
            this.dmsgServerContext = messageContext.getServerContext();
        }
        if (channelManager == null) {
            channelManager = messageContext.getServerContext().getUserChannelManager();
        }
        ChannelId channelId = messageContext.getChannelHandlerContext().channel().id();
        String username = channelManager.findUserByChannelId(channelId);
        if (StringUtils.isNotEmpty(username)) {
            SourceAddress sourceAddress = new SourceAddress();
            sourceAddress.setHost(dmsgServerContext.getHostDetail().getIp());
            sourceAddress.setPort(dmsgServerContext.getHostDetail().getPort());
            sourceAddress.setUser(username);
            messageContext.getMessage().setFrom(sourceAddress);
            messageContext.setBefrom(username);
            chain.doFilter(messageContext);
        } else {
            String msgId = messageContext.getMessage().getHeader().getMsgId();
            MessageBase messageBase = MessageBase.createAskMsg(false, msgId, "您还没有鉴权，请先鉴权");
            dmsgServerContext.getSender().send(messageContext.getChannelHandlerContext(), messageBase);
        }
    }

    public void destroy() {

    }

    public void init() {
        this.appendAttentionType(MessageType.SAVE_TEXT);
        this.appendAttentionType(MessageType.SEND_TEXT);
        this.appendAttentionType(MessageType.CLOSE);
    }

}

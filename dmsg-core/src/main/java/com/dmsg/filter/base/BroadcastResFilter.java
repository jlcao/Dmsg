package com.dmsg.filter.base;

import com.dmsg.data.HostDetail;
import com.dmsg.data.UserDetail;
import com.dmsg.filter.DmsgFilter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.BroadcastResMessage;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.message.vo.SourceAddress;
import com.dmsg.server.DmsgServerContext;

/**
 * Created by cjl on 2016/8/26.
 */
public class BroadcastResFilter extends DmsgFilter {
    DmsgServerContext serverContext;

    public void doFilter(MessageContext messageContext, FilterChain chain) {
        if (serverContext == null) {
            serverContext = messageContext.getServerContext();
        }
        MessageBase messageBase = messageContext.getMessage();
        BroadcastResMessage resMessage = (BroadcastResMessage) messageBase.getBody();
        SourceAddress sourceAddress = messageBase.getFrom();
        HostDetail hostDetail = serverContext.getHostCache().getHost(sourceAddress.keyFiled());
        if (hostDetail != null) {
            UserDetail userDetail = new UserDetail();
            userDetail.setLastTime(System.currentTimeMillis());
            userDetail.setLoginHost(hostDetail);
            userDetail.setStatus(1);
            userDetail.setUserName(resMessage.getUserName());
            serverContext.getUserCache().put(userDetail);
        }
        chain.doFilter(messageContext);
    }

    public void destroy() {

    }

    public void init() {
        this.appendAttentionType(MessageType.BROADCAST_RES);
    }
}

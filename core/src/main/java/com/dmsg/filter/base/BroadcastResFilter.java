package com.dmsg.filter.base;

import com.dmsg.filter.DmsgFilter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.MessageType;
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






    }

    public void destroy() {

    }

    public void init() {
        this.appendAttentionType(MessageType.BROADCAST_RES);
    }
}

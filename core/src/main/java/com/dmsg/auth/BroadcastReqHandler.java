package com.dmsg.auth;

import com.dmsg.filter.DmsgFilter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.MessageType;

/**
 * Created by cjl on 2016/8/15.
 */
public class BroadcastReqHandler extends DmsgFilter {
    public BroadcastReqHandler() {
        this.appendAttentionType(MessageType.BROADCAST_REQ);
    }

    public void doFilter(MessageContext messageContext, FilterChain chain) {

    }

    public void destroy() {

    }

    public void init() {

    }
}

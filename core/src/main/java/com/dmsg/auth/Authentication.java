package com.dmsg.auth;

import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.filter.DmsgFilter;
import com.dmsg.filter.Filter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.AuthReqMessage;
import com.dmsg.message.vo.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlcao on 2016/7/26.
 */
public class Authentication extends DmsgFilter {
    //需要过滤的类型

    public void doFilter(MessageContext messageContext, FilterChain chain) {
        if (MessageType.AUTH_REQ.equals(messageContext.getMessageType())) {
            AuthReqMessage authMessage = (AuthReqMessage) messageContext.getMessage().getBody();
            if ("123".equals(authMessage.getPassword())) {
                LocalUserChannelManager.getInstance().addContext(authMessage.getUsername(), messageContext.getChannelHandlerContext());
            }
        } else {
            chain.doFilter(messageContext);
        }
    }

    public void destroy() {

    }

    public void init() {

    }


}

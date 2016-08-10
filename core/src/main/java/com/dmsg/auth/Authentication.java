package com.dmsg.auth;

import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.filter.Filter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.AuthMessage;
import com.dmsg.message.vo.MessageType;

/**
 * Created by jlcao on 2016/7/26.
 */
public class Authentication implements Filter {
    public void doFilter(MessageContext messageContext, FilterChain chain) {
        if (MessageType.AUTH.equals(messageContext.getMessageType())) {
            AuthMessage authMessage = (AuthMessage) messageContext.getMessage();
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

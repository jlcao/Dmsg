package com.dmsg.filter;

import com.dmsg.message.MessageContext;

/**
 * Created by jlcao on 2016/7/26.
 */
public interface FilterChain{
    public boolean doFilter(MessageContext messageContext);
}

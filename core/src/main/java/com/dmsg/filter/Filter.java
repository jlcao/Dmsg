package com.dmsg.filter;

import com.dmsg.message.MessageContext;

/**
 * Created by jlcao on 2016/7/26.
 */
public interface Filter {

    public void doFilter(MessageContext messageContext, FilterChain chain);

    public void destroy();

    public void init();

    public int getOffe();
}

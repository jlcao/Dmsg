package com.dmsg.filter;

import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.MessageType;

import java.util.List;

/**
 * Created by jlcao on 2016/7/26.
 */
public interface Filter {

    public void doFilter(MessageContext messageContext, FilterChain chain);

    public void destroy();

    public void init();

    List<MessageType> attentionTypes();

    public Filter appendAttentionType(MessageType type);
}

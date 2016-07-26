package com.dmsg.filter;

import com.dmsg.message.MessageContext;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by jlcao on 2016/7/26.
 */
public class FilterChain extends ArrayList<Filter> {

    public void chain(MessageContext messageContext, Filter filter) {
        if (this.size() > filter.getOffe()) {
            this.get(filter.getOffe() + 1).doFilter(messageContext);
        } else {
            return;
        }


    }
}

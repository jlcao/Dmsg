package com.dmsg.route;

import com.dmsg.filter.DmsgFilter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.MessageContext;
import com.dmsg.server.DmsgServerContext;

/**
 * Created by jlcao on 2016/8/14.
 */
public class RouteFilter extends DmsgFilter {
    private RouteHandler routeHandler;

    public RouteFilter() {
        routeHandler = DmsgServerContext.getServerContext().getRouteHandler();

    }

    public void doFilter(MessageContext messageContext, FilterChain chain) {
        try {
            routeHandler.route(messageContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        chain.doFilter(messageContext);
    }

    public void destroy() {

    }

    public void init() {

    }
}

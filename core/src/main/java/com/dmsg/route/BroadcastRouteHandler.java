package com.dmsg.route;

import com.dmsg.cache.HostCache;
import com.dmsg.cache.UserCache;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.Header;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.route.vo.RouteMessage;
import com.dmsg.server.DmsgServerContext;

/**
 * 广播路由策略
 * Created by jlcao on 2016/8/2.
 */
public class BroadcastRouteHandler extends RouteHandler {

    private DmsgServerContext dmsgServerContext;
    private HostCache hostCache;
    private UserCache userCache;

    public BroadcastRouteHandler(DmsgServerContext dmsgServerContext) {
        this.dmsgServerContext = dmsgServerContext;
        userCache = dmsgServerContext.getUserCache();
        hostCache = dmsgServerContext.getHostCache();
    }

    public void route(MessageContext messageContext) {
        MessageBase messageBase = messageContext.getMessage();
        Header header = messageBase.getHeader();
        header.setMsgType(MessageType.BROADCAST_REQ.getVal());
        RouteMessage routeMessage = new RouteMessage();
        routeMessage.setMessage(messageBase);
        routeMessage.setHostDetails(hostCache.getAll());
    }
}

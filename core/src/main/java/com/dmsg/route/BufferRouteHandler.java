package com.dmsg.route;

import com.dmsg.cache.CacheManager;
import com.dmsg.data.HostDetail;
import com.dmsg.data.UserDetail;
import com.dmsg.message.MessageContext;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.route.vo.RouteMessage;
import com.dmsg.server.DmsgServerConfig;
import com.dmsg.server.DmsgServerContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存路由策略
 * Created by jlcao on 2016/8/2.
 */
public class BufferRouteHandler extends RouteHandler {
    CacheManager cacheManager;
    DmsgServerConfig config;

    public BufferRouteHandler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        config = DmsgServerContext.getServerContext().getConfig();
    }

    public BufferRouteHandler() {
        this(DmsgServerContext.getServerContext().getCache());
    }

    public RouteMessage route(MessageContext messageContext) throws Exception {
        RouteMessage routeMessage = new RouteMessage();
        MessageBase message = messageContext.getMessage();
        if (message == null) {
            throw new Exception("route message is null!");
        }
        routeMessage.setMessage(message);
        //通过三方缓存获取用户相关信息
        UserDetail userDetail = cacheManager.getUserByName(config.getUserNodeFlag(), message.getReceiver());
        if (userDetail != null) {
            List<HostDetail> hosts = new ArrayList<HostDetail>();
            hosts.add(userDetail.getLoginHost());
            routeMessage.setHostDetails(hosts);
        } else {
            //信息获取失败，广播



        }


        return routeMessage;
    }
}

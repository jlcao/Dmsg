package com.dmsg.route;

import com.dmsg.cache.HostCache;
import com.dmsg.cache.UserCache;
import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.data.HostDetail;
import com.dmsg.data.UserDetail;
import com.dmsg.message.MessageContext;
import com.dmsg.message.MessageSender;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.route.vo.RouteMessage;
import com.dmsg.server.DmsgServerConfig;
import com.dmsg.server.DmsgServerContext;

/**
 * 缓存路由策略
 * Created by jlcao on 2016/8/2.
 */
public class BufferRouteHandler extends RouteHandler {
    UserCache userCache;
    DmsgServerConfig config;
    HostCache hostCache;
    MessageSender sender;
    LocalUserChannelManager localUserChannelManager;
    HostDetail localHost;


    public BufferRouteHandler(DmsgServerContext dmsgServerContext) {
        userCache = dmsgServerContext.getUserCache();
        config = dmsgServerContext.getConfig();
        //broadcastRouteHandler = new BroadcastRouteHandler(dmsgServerContext);
        hostCache = dmsgServerContext.getHostCache();
        sender = dmsgServerContext.getSender();
        this.localHost = dmsgServerContext.getHostDetail();
        localUserChannelManager = dmsgServerContext.getUserChannelManager();
    }



    public void route(MessageContext messageContext) throws Exception {
        RouteMessage routeMessage = new RouteMessage();
        MessageBase message = messageContext.getMessage();
        if (message == null) {
            throw new Exception("route message is null!");
        }
        routeMessage.setMessage(message);
        //通过三方缓存获取用户相关信息
        String[] users = message.getTo().getUsers();
        for (String user : users) {
            if (localUserChannelManager.isAvailable(user)) {
                sender.send(localUserChannelManager.getContext(user), message);
            } else {
                UserDetail userDetail = userCache.getUserByName(user);
                if (userDetail != null) {
                    routeMessage.addHost(userDetail.getLoginHost());
                } else {
                    //离线消息|广播
                    RouteMessage tmp = generateBroadcastRoute(message, user);
                    sender.send(tmp);
                }
            }
        }
        sender.send(routeMessage);
    }

    private RouteMessage generateBroadcastRoute(MessageBase message, String user) {
        MessageBase messageBase = MessageBase.createBroadcastReq(user, message, localHost);
        RouteMessage tmp = new RouteMessage();
        tmp.setMessage(messageBase);
        tmp.setHostDetails(hostCache.getAll());
        return tmp;
    }
}

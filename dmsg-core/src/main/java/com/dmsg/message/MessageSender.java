package com.dmsg.message;

import com.alibaba.fastjson.JSON;
import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.channel.RemoteHostChannelManager;
import com.dmsg.data.HostAskMessage;
import com.dmsg.data.HostDetail;
import com.dmsg.data.OfflineMessage;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.route.vo.RouteMessage;
import com.dmsg.server.DmsgServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Set;

/**
 * Created by jlcao on 2016/8/10.
 */
public class MessageSender {
    DmsgServerContext dmsgServerContext;
    RemoteHostChannelManager remotHostChannelManager;
    LocalUserChannelManager userChannelManager;

    public MessageSender(DmsgServerContext dmsgServerContext) {
        this.dmsgServerContext = dmsgServerContext;
        remotHostChannelManager = dmsgServerContext.getRemotHostsChannelManager();
        userChannelManager = dmsgServerContext.getUserChannelManager();
    }

    public void send(RouteMessage route) {
        Set<HostDetail> hosts = route.getHostDetails();
        if (hosts != null && !hosts.isEmpty()) {
            MessageBase messageBase = route.getMessage();
            for (HostDetail hostDetail : hosts) {
                send(remotHostChannelManager.getContext(hostDetail.keyFiled()), messageBase);
            }
        }

    }

    public void send(ChannelHandlerContext context, MessageBase messageBase) {
        if (context != null) {
            context.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messageBase)));
        }

    }

    public void send(OfflineMessage offlineMessage) {
        send(userChannelManager.getContext(offlineMessage.getUsername()), offlineMessage.getMessage());
    }

    public void send(HostAskMessage offlineMessage) {
        send(remotHostChannelManager.getContext(offlineMessage.getHost().keyFiled()), offlineMessage.getMessage());
    }
}

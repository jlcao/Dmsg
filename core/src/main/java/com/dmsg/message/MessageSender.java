package com.dmsg.message;

import com.alibaba.fastjson.JSON;
import com.dmsg.channel.RemotHostChannelManager;
import com.dmsg.data.HostDetail;
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
    RemotHostChannelManager remotHostChannelManager;

    public MessageSender(DmsgServerContext dmsgServerContext) {
        this.dmsgServerContext = dmsgServerContext;
        remotHostChannelManager = dmsgServerContext.getRemotHostsChannelManager();
    }

    public void send(RouteMessage route) {
        Set<HostDetail> hosts = route.getHostDetails();
        MessageBase messageBase = route.getMessage();
        for (HostDetail hostDetail : hosts) {
            send(remotHostChannelManager.getContext(hostDetail.keyFiled()), messageBase);
        }
    }

    public void send(ChannelHandlerContext context, MessageBase messageBase) {
        if (context != null) {
            context.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messageBase)));
        }

    }
}

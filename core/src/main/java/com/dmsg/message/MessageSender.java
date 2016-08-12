package com.dmsg.message;

import com.dmsg.data.HostDetail;
import com.dmsg.route.vo.RouteMessage;
import com.dmsg.server.DmsgServerContext;

import java.util.List;

/**
 * Created by jlcao on 2016/8/10.
 */
public class MessageSender {
    DmsgServerContext dmsgServerContext;
    public void send(RouteMessage route) {
        List<HostDetail> hosts = route.getHostDetails();




    }
}

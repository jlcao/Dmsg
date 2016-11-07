package com.dmsg.route.vo;

import com.dmsg.data.HostDetail;
import com.dmsg.data.UserDetail;
import com.dmsg.message.vo.MessageBase;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cjl on 2016/7/27.
 */
public class RouteMessage {
    private MessageBase message;
    private Set<HostDetail> hostDetails;
    private UserDetail user;

    public MessageBase getMessage() {
        return message;
    }

    public void setMessage(MessageBase message) {
        this.message = message;
    }

    public UserDetail getUser() {
        return user;
    }

    public void setUser(UserDetail user) {
        this.user = user;
    }

    public Set<HostDetail> getHostDetails() {
        return hostDetails;
    }

    public void setHostDetails(Set<HostDetail> hostDetails) {
        this.hostDetails = hostDetails;
    }

    public void addHost(HostDetail loginHost) {
        if (hostDetails == null) {
            hostDetails = new HashSet<HostDetail>();
        }
        hostDetails.add(loginHost);
    }
}

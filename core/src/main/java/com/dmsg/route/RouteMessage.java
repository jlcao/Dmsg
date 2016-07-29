package com.dmsg.route;

import com.dmsg.data.UserDetail;
import com.dmsg.message.vo.MessageBase;

/**
 * Created by cjl on 2016/7/27.
 */
public class RouteMessage {
    private MessageBase message;
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
}

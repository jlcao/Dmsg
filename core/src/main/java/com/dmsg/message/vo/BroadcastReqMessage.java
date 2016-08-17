package com.dmsg.message.vo;

/**
 * Created by jlcao on 2016/8/17.
 */
public class BroadcastReqMessage extends MessageBody {

    private String userName;
    private MessageBase message;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public MessageBase getMessage() {
        return message;
    }

    public void setMessage(MessageBase message) {
        this.message = message;
    }
}

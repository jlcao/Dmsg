package com.dmsg.message.vo;

/**
 * Created by jlcao on 2016/8/17.
 */
public class BroadcastResMessage extends MessageBody {

    private String msgId;
    private String userName;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

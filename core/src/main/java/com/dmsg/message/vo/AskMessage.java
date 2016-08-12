package com.dmsg.message.vo;

/**
 * Created by jlcao on 2016/8/2.
 */
public class AskMessage extends MessageBody{
    private String msgId;
    private Boolean succ;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Boolean getSucc() {
        return succ;
    }

    public void setSucc(Boolean succ) {
        this.succ = succ;
    }
}

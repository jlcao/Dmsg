package com.dmsg.data;

import com.dmsg.message.vo.MessageBase;

/**
 * Created by cjl on 2016/7/28.
 */
public class OfflineMessage {

    private long msgId;

    private boolean flag;

    private int retrySize = 0;

    private MessageBase message;
    private String username;

    public MessageBase getMessage() {
        return message;
    }

    public void setMessage(MessageBase message) {
        this.message = message;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getRetrySize() {
        return retrySize;
    }

    public void retry() {
        retrySize++;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

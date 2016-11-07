package com.dmsg.data;

import com.dmsg.message.vo.MessageBase;

/**
 * Created by cjl on 2016/7/28.
 */
public class HostAskMessage {

    private long msgId;

    private boolean flag;

    private int retrySize = 0;

    private MessageBase message;
    private HostDetail host;

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

    public void setHost(HostDetail host) {
        this.host = host;
    }

    public HostDetail getHost() {
        return host;
    }
}

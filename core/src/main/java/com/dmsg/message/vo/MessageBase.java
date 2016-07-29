package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public abstract class MessageBase {
    private long messageId;
    private String type;
    private String beFrom;
    private String receiver;


    public MessageBase(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract Object getBody();

    public String getBeFrom() {
        return beFrom;
    }

    public void setBeFrom(String beFrom) {
        this.beFrom = beFrom;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
}

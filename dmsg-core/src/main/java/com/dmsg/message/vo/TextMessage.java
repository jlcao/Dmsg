package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public class TextMessage extends MessageBody {

    private String content;

    private String[] receivers;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }
}

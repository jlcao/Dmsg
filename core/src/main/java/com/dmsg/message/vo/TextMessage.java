package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public class TextMessage extends MessageBody {

    private String content;

    private String receiver;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}

package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public class TextMessage extends MessageBase {
    private String receiver;
    private String text;


    public TextMessage(String text) {
        super(MessageType.TEXT.getCode());
        this.text = text;
    }

    @Override
    public Object getBody() {
        return this;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

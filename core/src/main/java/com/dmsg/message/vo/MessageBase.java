package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public class MessageBase {

    private Header header;
    private MessageBody body;
    //源地址
    private SourceAddress from;
    //目的地
    private SourceAddress to;

    public MessageBase() {
    }


    public MessageBase(MessageBody body) {
        this.body = body;
    }

    public MessageBase(Header header, MessageBody body) {
        this.header = header;
        this.body = body;
    }

    public MessageBase(Header header) {
        this.header = header;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public MessageBody getBody() {
        return body;
    }

    public void setBody(MessageBody body) {
        this.body = body;
    }

    public SourceAddress getFrom() {
        return from;
    }

    public void setFrom(SourceAddress from) {
        this.from = from;
    }

    public SourceAddress getTo() {
        return to;
    }

    public void setTo(SourceAddress to) {
        this.to = to;
    }
}

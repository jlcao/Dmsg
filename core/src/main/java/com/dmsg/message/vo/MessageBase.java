package com.dmsg.message.vo;

import com.dmsg.data.HostDetail;

/**
 * Created by cjl on 2016/7/11.
 */
public class MessageBase {

    private Header header;
    private MessageBody body;
    //源地址
    private SourceAddress from;
    //目的地
    private DestAddress to;

    public MessageBase() {
    }

    @Override
    public MessageBase clone() {
        try {
            return (MessageBase)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
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

    public DestAddress getTo() {
        return to;
    }

    public void setTo(DestAddress to) {
        this.to = to;
    }

    public static MessageBase createAuthReq(AuthResMessage b,HostDetail local) {
        MessageBase messageBase = new MessageBase();

        SourceAddress sourceAddress = new SourceAddress();
        sourceAddress.setHost(local.getIp());
        sourceAddress.setPort(local.getPort());
        Header header = new Header();
        header.setMsgType(MessageType.AUTH_RES.getVal());

        messageBase.setBody(b);
        messageBase.setFrom(new SourceAddress());
        messageBase.setHeader(header);

        return messageBase;
    }

    public static MessageBase createBroadcastRes(String msgId, String user, HostDetail local) {
        MessageBase messageBase = new MessageBase();
        SourceAddress sourceAddress = new SourceAddress();
        sourceAddress.setHost(local.getIp());
        sourceAddress.setPort(local.getPort());

        Header header = new Header();
        header.setMsgType(MessageType.BROADCAST_RES.getVal());
        BroadcastResMessage broadcastResMessage = new BroadcastResMessage();
        broadcastResMessage.setMsgId(msgId);
        broadcastResMessage.setUserName(user);
        messageBase.setBody(broadcastResMessage);
        messageBase.setHeader(header);
        messageBase.setFrom(sourceAddress);

        return messageBase;
    }

    public static MessageBase createBroadcastReq(String user, MessageBase message) {
        MessageBase messageBase = new MessageBase();

        Header header = new Header();
        header.setAuthKey(messageBase.getHeader().getAuthKey());
        header.setCall(messageBase.getHeader().getCall());
        header.setMsgId(messageBase.getHeader().getMsgId());
        header.setMsgType(MessageType.BROADCAST_REQ.getVal());

        BroadcastReqMessage broadcastReqMessage = new BroadcastReqMessage();
        broadcastReqMessage.setUserName(user);
        broadcastReqMessage.setMessage(message);
        messageBase.setBody(broadcastReqMessage);
        messageBase.setHeader(header);


        return messageBase;
    }
}

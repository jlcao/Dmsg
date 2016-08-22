package com.dmsg.message.vo;

import com.dmsg.data.HostDetail;
import com.dmsg.server.DmsgServerContext;

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

    public static MessageBase createAuthReq(AuthReqMessage b,HostDetail local) {
        SourceAddress sourceAddress = new SourceAddress();
        sourceAddress.setHost(local.getIp());
        sourceAddress.setPort(local.getPort());
        Header header = new Header();
        header.setMsgType(MessageType.AUTH_REQ.getVal());
        header.setAuthKey(DmsgServerContext.getServerContext().getConfig().getServerAuthKey());

        return createMessage(b,sourceAddress,header);
    }

    public static MessageBase createBroadcastRes(String msgId, String user, HostDetail local) {
        SourceAddress sourceAddress = new SourceAddress();
        sourceAddress.setHost(local.getIp());
        sourceAddress.setPort(local.getPort());

        Header header = new Header();
        header.setMsgType(MessageType.BROADCAST_RES.getVal());

        BroadcastResMessage broadcastResMessage = new BroadcastResMessage();
        broadcastResMessage.setMsgId(msgId);
        broadcastResMessage.setUserName(user);


        return createMessage(broadcastResMessage,sourceAddress,header);
    }

    public static MessageBase createBroadcastReq(String user, MessageBase message, HostDetail local) {

        Header header = new Header();
        header.setAuthKey(message.getHeader().getAuthKey());
        header.setCall(message.getHeader().getCall());
        header.setMsgId(message.getHeader().getMsgId());
        header.setMsgType(MessageType.BROADCAST_REQ.getVal());

        SourceAddress sourceAddress = new SourceAddress();
        sourceAddress.setHost(local.getIp());
        sourceAddress.setPort(local.getPort());

        BroadcastReqMessage broadcastReqMessage = new BroadcastReqMessage();
        broadcastReqMessage.setUserName(user);
        broadcastReqMessage.setMessage(message);


        return createMessage(broadcastReqMessage,sourceAddress, header);
    }

    public static MessageBase createAuthRes(AuthResMessage authResMessage, HostDetail local) {

        SourceAddress sourceAddress = new SourceAddress();
        sourceAddress.setHost(local.getIp());
        sourceAddress.setPort(local.getPort());
        Header header = new Header();
        header.setMsgType(MessageType.AUTH_RES.getVal());
        return createMessage(authResMessage,sourceAddress,header);
    }

    public static MessageBase createMessage(MessageBody body,SourceAddress sourceAddress,Header header){
        MessageBase messageBase = new MessageBase();
        messageBase.setBody(body);
        messageBase.setFrom(sourceAddress);
        messageBase.setHeader(header);

        return messageBase;
    }

    @Override
    public String toString() {
        return "MessageBase{" +
                "header=" + header +
                ", body=" + body +
                ", from=" + from +
                ", to=" + to +
                '}';
    }

    public static MessageBase createAskMsg(boolean b, String msgId, String s) {
        AskMessage askMessage = new AskMessage();
        askMessage.setMsg(s);
        askMessage.setMsgId(msgId);
        askMessage.setSucc(b);

        Header header = new Header();
        header.setMsgType(MessageType.MSG_ACK.getVal());


        return createMessage(askMessage, null, header);
    }
}

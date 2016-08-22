package com.dmsg.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dmsg.message.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cjl on 2016/7/12.
 */
public class MessageParser {
    public static final String HEADER = "header";
    public static final String BODY = "body";
    public static final String TO = "to";
    public static final String FROM = "from";
    public Logger logger = LoggerFactory.getLogger(this.getClass());


    public void parse(MessageContext messageContext) {
        String source = messageContext.getSource();
        MessageBase message = parse(source);
        messageContext.setMessage(message);
        messageContext.setMessageType(MessageType.getByVal(message.getHeader().getMsgType()));
    }

    private MessageBase parse(String source) {
        logger.debug(source);
        JSONObject data = JSON.parseObject(source);
        Header header = data.getObject(HEADER, Header.class);
        MessageType type = MessageType.getByVal(header.getMsgType());
        MessageBase result = new MessageBase(header);
        if (data.containsKey(TO)) {
            DestAddress destAddress = data.getObject(TO, DestAddress.class);
            result.setTo(destAddress);
        }
        if (data.containsKey(FROM)) {
            SourceAddress sourceAddress = data.getObject(FROM, SourceAddress.class);
            result.setFrom(sourceAddress);
        }


        MessageBody body = null;
        switch (type) {
            case AUTH_REQ:
                body = data.getObject(BODY, AuthReqMessage.class);
                break;
            case AUTH_RES:
                body = data.getObject(BODY, AuthResMessage.class);
                break;
            case SEND_TEXT:
                body = data.getObject(BODY, TextMessage.class);
                break;
            case SAVE_TEXT:
                DestAddress destAddress = new DestAddress();
                TextMessage tmp = data.getObject(BODY, TextMessage.class);
                destAddress.setUsers(tmp.getReceivers());
                result.setTo(destAddress);
                body = tmp;
                break;
            case MSG_ACK:
                body = data.getObject(BODY, AskMessage.class);
                break;
            case BROADCAST_REQ:
                JSONObject jsonBody = data.getJSONObject(BODY);
                BroadcastReqMessage broadcastReqMessage = new BroadcastReqMessage();
                broadcastReqMessage.setUserName(jsonBody.getString("userName"));
                MessageBase messageBase = parse(jsonBody.getJSONObject("message"));
                broadcastReqMessage.setMessage(messageBase);
                body = broadcastReqMessage;

                break;
            case BROADCAST_RES:
                body = data.getObject(BODY, BroadcastResMessage.class);
                break;
            case CLOSE:
                body = data.getObject(BODY, ConnectionCloseMessage.class);
                break;
        }
        result.setBody(body);
        return result;
    }

    private MessageBase parse(JSONObject data) {
        Header header = data.getObject(HEADER, Header.class);
        MessageType type = MessageType.getByVal(header.getMsgType());
        MessageBase result = new MessageBase(header);
        if (data.containsKey(TO)) {
            DestAddress destAddress = data.getObject(TO, DestAddress.class);
            result.setTo(destAddress);
        }
        if (data.containsKey(FROM)) {
            SourceAddress sourceAddress = data.getObject(FROM, SourceAddress.class);
            result.setFrom(sourceAddress);
        }


        MessageBody body = null;
        switch (type) {
            case AUTH_REQ:
                body = data.getObject(BODY, AuthReqMessage.class);
                break;
            case AUTH_RES:
                body = data.getObject(BODY, AuthResMessage.class);
                break;
            case SEND_TEXT:
                body = data.getObject(BODY, TextMessage.class);
                break;
            case SAVE_TEXT:
                DestAddress destAddress = new DestAddress();
                TextMessage tmp = data.getObject(BODY, TextMessage.class);
                destAddress.setUsers(tmp.getReceivers());
                result.setTo(destAddress);
                body = tmp;
                break;
            case MSG_ACK:
                body = data.getObject(BODY, AskMessage.class);
                break;
            case BROADCAST_REQ:
                JSONObject jsonBody = data.getJSONObject(BODY);
                BroadcastReqMessage broadcastReqMessage = new BroadcastReqMessage();
                broadcastReqMessage.setUserName(jsonBody.getString("username"));
                MessageBase messageBase = parse(jsonBody.getJSONObject(BODY));
                broadcastReqMessage.setMessage(messageBase);
                body = broadcastReqMessage;
                break;
            case BROADCAST_RES:
                body = data.getObject(BODY, BroadcastResMessage.class);
                break;
            case CLOSE:
                body = data.getObject(BODY, ConnectionCloseMessage.class);
                break;
        }
        result.setBody(body);
        return result;
    }


}

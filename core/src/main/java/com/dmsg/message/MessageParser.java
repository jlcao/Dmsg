package com.dmsg.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dmsg.message.vo.*;

/**
 * Created by cjl on 2016/7/12.
 */
public class MessageParser {
    public static final String KEY_FIELD_USERNAME = "username";
    public static final String KEY_FIELD_PASSWORD = "password";
    public static final String KEY_FIELD_TYPE = "type";
    public static final String KEY_FIELD_RECEIVER = "receiver";
    public static final String KEY_FIELD_TEXT = "text";


    public void parse(MessageContext messageContext) {
        MessageBase source = messageContext.getSource();
        MessageType type = MessageType.forCode(source.getType());
        MessageBase message = parse(source,type);
        messageContext.setMessage(message);
        messageContext.setMessageType(MessageType.forCode(message.getType()));

    }

    private MessageBase parse(MessageBase message,MessageType type) {
        MessageBase result = null;
        switch (type) {
            case AUTH:
            case SHAKE:
            case TEXT:
                result = parseText((TextMessage) message);
                break;
            case FILE:
                result = parseFile((FileMessage) message);
                break;
            case CONTROLLER_CLOSE:
                result = parseController((ControllerMessage) message);
                break;
        }
        return result;
    }

    private ControllerMessage parseController(ControllerMessage message) {
        return message;
    }

    private FileMessage parseFile(FileMessage message) {
        return message;
    }

    private MessageBase parseText(TextMessage message) {
        String content = message.getText();
        JSONObject jsonObject = JSON.parseObject(content);
        MessageBase messageBase = null;
        MessageType type = MessageType.forCode(jsonObject.getString(KEY_FIELD_TYPE));

        switch (type) {
            case AUTH:
                messageBase = parseAuth(jsonObject);
                break;
            case TEXT:
                messageBase = parseMessageText(jsonObject);
                break;
            case SHAKE:
                break;
        }


        return messageBase;
    }

    private MessageBase parseMessageText(JSONObject jsonObject) {
        TextMessage textMessage = new TextMessage("");
        textMessage.setReceiver(jsonObject.getString(KEY_FIELD_RECEIVER));
        textMessage.setText(jsonObject.getString(KEY_FIELD_TEXT));
        return textMessage;
    }

    private MessageBase parseAuth(JSONObject jsonObject) {
        AuthMessage authMessage = new AuthMessage();
        authMessage.setUsername(jsonObject.getString(KEY_FIELD_USERNAME));
        authMessage.setPassword(jsonObject.getString(KEY_FIELD_PASSWORD));
        return authMessage;
    }


}

package com.dmsg.message.vo;

/**
 * Created by jlcao on 2016/8/2.
 */
public class AskMessage extends MessageBase{


    public AskMessage() {
        super(MessageType.ASK.getCode());
    }

    public Object getBody() {
        return null;
    }
}

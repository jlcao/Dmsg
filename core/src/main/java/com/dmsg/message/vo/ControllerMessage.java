package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public class ControllerMessage extends MessageBase {


    public ControllerMessage(String type) {
        super(type);
    }

    @Override
    public Object getBody() {
        return this;
    }
}

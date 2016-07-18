package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public abstract class MessageBase {
    private String type;
    private String beFrom;


    public MessageBase(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract Object getBody();

    public String getBeFrom() {
        return beFrom;
    }

    public void setBeFrom(String beFrom) {
        this.beFrom = beFrom;
    }
}

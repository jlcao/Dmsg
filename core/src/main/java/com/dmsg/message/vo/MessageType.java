package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public enum MessageType {
    AUTH("AUTH","鉴权"),
    CONTROLLER("CONTROLLER", "控制"),
    SHAKE("SHAKE", "抖动"),
    CONTROLLER_CLOSE("CONTROLLER_CLOSE", "关闭链接"),
    TEXT("TEXT","文本"),
    FILE("FILE", "文件");

    private String code;
    private String desc;

    MessageType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static MessageType forCode(String type) {
        for (MessageType tmp : MessageType.values()) {
            if (tmp.getCode().equals(type)) {
                return tmp;
            }
        }
        return null;
    }
}

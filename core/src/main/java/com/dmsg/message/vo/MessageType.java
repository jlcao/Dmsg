package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public enum MessageType {
    AUTH_REQ(1,"鉴权请求"),
    AUTH_RES(2, "鉴权响应"),
    SEND_TEXT(3, "消息透传"),
    SAVE_TEXT(4, "存储（&转发）消息"),
    MSG_ACK(5,"消息回执确认"),
    CLOSE(6,"关闭连接");

    private int val;
    private String desc;

    MessageType(int val, String desc) {
        this.val = val;
        this.desc = desc;
    }



    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public static MessageType getByVal(int msgType) {
        for (MessageType tmp : MessageType.values()) {
            if (tmp.getVal() == msgType) {
                return tmp;
            }
        }
        return null;
    }
}

package com.dmsg.message.vo;

import com.dmsg.data.HostDetail;

/**
 * Created by jlcao on 2016/8/2.
 */
public class AskMessage extends MessageBase{

    private HostDetail hostDetail;


    public AskMessage() {
        super(MessageType.ASK.getCode());
    }

    public Object getBody() {
        return null;
    }

    public HostDetail getHostDetail() {
        return hostDetail;
    }

    public void setHostDetail(HostDetail hostDetail) {
        this.hostDetail = hostDetail;
    }
}

package com.dmsg.filter;

import com.dmsg.message.vo.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlcao on 2016/8/12.
 */
public abstract class DmsgFilter implements Filter {

    private List<MessageType> attentionType = null;

    public DmsgFilter(List<MessageType> attentionType) {
        this.attentionType = attentionType;
    }

    public DmsgFilter() {
        this.attentionType = new ArrayList<MessageType>();
    }

    public Filter appendAttentionType(MessageType type) {
        attentionType.add(type);
        return this;
    }

    public List<MessageType> attentionTypes() {
        return attentionType;
    }


}

package com.dmsg.message.vo;

import io.netty.buffer.ByteBuf;

/**
 * Created by cjl on 2016/7/11.
 */
public class FileMessage extends MessageBase {
    private ByteBuf byteBuf;

    public FileMessage() {
        super(MessageType.FILE.getCode());
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public void setByteBuf(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public Object getBody() {
        return this;
    }
}

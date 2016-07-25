package com.dmsg.message;

import com.alibaba.fastjson.JSON;
import com.dmsg.channel.LocalChannelManager;
import com.dmsg.exception.AuthenticationException;
import com.dmsg.message.vo.AuthMessage;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.message.vo.TextMessage;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cjl on 2016/7/11.
 */
public class MessageHandler implements Runnable{
    Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    MessageContext messageContext;
    MessageBase message;
    MessageParser parser;
    LocalChannelManager channelManager;
    ChannelId channelId;



    public MessageHandler(MessageContext messageContext) {
        this.messageContext = messageContext;

        channelId = messageContext.getChannelHandlerContext().channel().id();
        parser = new MessageParser();
        channelManager = LocalChannelManager.getInstance();
    }

    public void run() {
        System.out.println("收到请求：" + messageContext.getSource().getType());
        parser.parse(messageContext);
        message = messageContext.getMessage();
        System.out.println("收到请求：" + message.getBody());
        try {
            process();

        } catch (AuthenticationException e) {
            messageContext.getChannelHandlerContext().channel().writeAndFlush(new TextWebSocketFrame(e.getMessage().toString()));
        }

    }

    private void process() throws AuthenticationException {
        //鉴权
        if (!messageContext.getMessageType().getCode().equals(MessageType.AUTH.getCode())) {
            auth();
        }
        switch (messageContext.getMessageType()) {
            case AUTH:
                processAuth();
                break;
            case TEXT:
                processText();
                break;
            case CONTROLLER_CLOSE:
                processClose();
                break;
        }


    }

    private void processClose() {
        channelManager.removeContext(message.getBeFrom());
    }

    private void processText() {
        //判断用户是否在线
        if (channelManager.isAvailable(message.getBeFrom())) {
            send();
        } else {
            //离线消息存储

        }

    }

    private void send() {
        TextMessage textMessage = (TextMessage) message;
        channelManager.getContext(textMessage.getReceiver()).channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(textMessage)));
    }

    /**
     * 鉴权处理
     * （后续独立处理）
     */

    private void processAuth() {
        AuthMessage authMessage = (AuthMessage) message;
        messageContext.authentication(authMessage);
        channelManager.addContext(authMessage.getUsername(), messageContext.getChannelHandlerContext());

        System.out.println("登录成功"+authMessage);
        messageContext.getChannelHandlerContext().channel().writeAndFlush(new TextWebSocketFrame("登陆成功"));
    }

    /**
     * 判断用户鉴权信息
     * @throws AuthenticationException
     */
    private void auth() throws AuthenticationException {
        String username = channelManager.findUserByChannelId(channelId);
        if (StringUtils.isNotEmpty(username)) {
            message.setBeFrom(username);
        } else {
            throw new AuthenticationException("用户还没有鉴权！");
        }
    }
}

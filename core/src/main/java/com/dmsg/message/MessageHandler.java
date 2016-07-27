package com.dmsg.message;

import com.alibaba.fastjson.JSON;
import com.dmsg.channel.LocalChannelManager;
import com.dmsg.exception.AuthenticationException;
import com.dmsg.filter.Filter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.vo.AuthMessage;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.message.vo.TextMessage;
import com.dmsg.route.RouteHandler;
import com.dmsg.route.RouteMessage;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjl on 2016/7/11.
 */
public class MessageHandler implements Runnable{
    Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    MessageContext messageContext;
    MessageBase message;

    final List<Filter> filters;
    LocalChannelManager channelManager;
    ChannelId channelId;


    public MessageHandler(MessageContext messageContext) {
        this.messageContext = messageContext;
        message = messageContext.getMessage();
        channelId = messageContext.getChannelHandlerContext().channel().id();
        filters = new ArrayList<Filter>();
        channelManager = LocalChannelManager.getInstance();
    }

    public void run() {
        System.out.println("收到请求：" + messageContext.getSource().getType());
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
        FilterChain chain = getFilterChain();
        boolean bl = chain != null ? chain.doFilter(messageContext) : true;
        if (bl) {
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
    }

    /**
     * 1.获取用户所在主机
         路由策略
             1.1 广播-缓存策略 广播到其它主机-存在该用户的主机响应-缓存该用户所在的主机在本地缓存(需要设置本地缓存失效时间)。
             1.2 注册-缓存策略 每个用户鉴权时将用户信息注册到共享缓存服务器-查找到用户主机时缓存到本地缓存(需要设置本地缓存失效时间)。
     * 2.如果没有任何主机响应(1.1策略)或存在(1.2策略),则离线缓存消息,定时任务处理
     **/
    private RouteMessage routeMessage() {
        RouteHandler routeHandler = RouteHandler.getHandler();


        return routeHandler.route(messageContext);
    }

    private void processClose() {
        channelManager.removeContext(message.getBeFrom());
    }

    private void processText() {
        //判断接收用户是否在本地登陆
        if (channelManager.isAvailable(message.getReceiver())) {
            send();
        } else {
            routeMessage();
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
        if (messageContext.authentication(authMessage)) {
            channelManager.addContext(authMessage.getUsername(), messageContext.getChannelHandlerContext());
            System.out.println("登录成功" + authMessage);
            messageContext.getChannelHandlerContext().channel().writeAndFlush(new TextWebSocketFrame("登陆成功"));
        } else {

        }




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

    public FilterChain getFilterChain() {
        if (filters != null && !filters.isEmpty()) {
            FilterChain chain = new Chain(filters);
        }


        return null;
    }

    private class Chain implements FilterChain{
        final List<Filter> filters;
        int _filter= 0;

        private Chain(List<Filter> filters) {
            this.filters = filters;
        }

        public boolean doFilter(MessageContext messageContext) {
            boolean bl = true;
            if (_filter < filters.size()) {
                Filter filter = filters.get(_filter++);
                filter.doFilter(messageContext, this);
            }

            if (_filter < filters.size()) {
                bl = false;
            }
            return bl;
        }
    }

}

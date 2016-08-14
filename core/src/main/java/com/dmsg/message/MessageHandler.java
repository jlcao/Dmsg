package com.dmsg.message;

import com.alibaba.fastjson.JSON;
import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.exception.AuthenticationException;
import com.dmsg.filter.Filter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.vo.AuthReqMessage;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.message.vo.MessageType;
import com.dmsg.message.vo.TextMessage;
import com.dmsg.route.RouteHandler;
import com.dmsg.route.vo.RouteMessage;
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
public class MessageHandler implements Runnable {
    private final List<Filter> filters;
    Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private MessageContext messageContext;
    private MessageBase message;
    private LocalUserChannelManager channelManager;
    private MessageSender messageSender;
    private MessageOfflineHandler messageOfflineHandler;
    private ChannelId channelId;


    public MessageHandler(MessageContext messageContext) {
        this.messageContext = messageContext;
        message = messageContext.getMessage();
        channelId = messageContext.getChannelHandlerContext().channel().id();
        filters = new ArrayList<Filter>();
        channelManager = LocalUserChannelManager.getInstance();
    }

    public void run() {
        try {
            process();
        } catch (Exception e) {
            messageContext.getChannelHandlerContext().channel().writeAndFlush(new TextWebSocketFrame(e.getMessage().toString()));
        }


    }

    private void process() throws Exception {
        //鉴权
        if (!messageContext.getMessageType().equals(MessageType.AUTH_REQ)) {
            auth();
        }
        FilterChain chain = getFilterChain();
        chain.doFilter(messageContext);
    }

    /**
     * 1.获取用户所在主机
     * 路由策略
     * 1.1 广播-缓存策略 广播到其它主机-存在该用户的主机响应-缓存该用户所在的主机在本地缓存(需要设置本地缓存失效时间)。
     * 1.2 注册-缓存策略 每个用户鉴权时将用户信息注册到共享缓存服务器-查找到用户主机时缓存到本地缓存(需要设置本地缓存失效时间)。
     * 2.如果没有任何主机响应(1.1策略)或存在(1.2策略),则离线缓存消息,定时任务处理
     **/
    private RouteMessage routeMessage() throws Exception {
        RouteHandler routeHandler = RouteHandler.getHandler();
        return routeHandler.route(messageContext);
    }

    private void processClose() {
        channelManager.removeContext(messageContext.getChannelHandlerContext());
    }

    private void processText() throws Exception {
        RouteMessage route = routeMessage();
        if (route != null) {
            //发送消息
            messageSender.send(route);
        } else {
            //离线缓存消息
            messageOfflineHandler.offlineMessage(message);
        }

    }

    private void send() {
        TextMessage textMessage = (TextMessage) message.getBody();
        channelManager.getContext(textMessage.getReceiver()).channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(textMessage)));
    }

    /**
     * 鉴权处理
     * （后续独立处理）
     */

    private void processAuth() {
        AuthReqMessage authMessage = (AuthReqMessage) message.getBody();
        if (messageContext.authentication(authMessage)) {
            channelManager.addContext(authMessage.getUsername(), messageContext.getChannelHandlerContext());
            messageContext.getChannelHandlerContext().channel().writeAndFlush(new TextWebSocketFrame("登陆成功"));
        } else {

        }


    }

    /**
     * 判断用户鉴权信息
     *
     * @throws AuthenticationException
     */
    private void auth() throws AuthenticationException {
        String username = channelManager.findUserByChannelId(channelId);
        if (StringUtils.isNotEmpty(username)) {
            messageContext.setBefrom(username);
        } else {
            throw new AuthenticationException("用户还没有鉴权！");
        }
    }

    public FilterChain getFilterChain() {
        FilterChain chain = null;
        if (filters != null && !filters.isEmpty()) {
            chain = new Chain(filters);
        }
        return chain;
    }

    private class Chain implements FilterChain {
        final List<Filter> filters;
        int _filter = 0;
        private Chain(List<Filter> filters) {
            this.filters = filters;
        }
        public boolean doFilter(MessageContext messageContext) {
            boolean bl = true;
            if (_filter < filters.size()) {
                Filter filter = filters.get(_filter++);

                if (messageTypeCheck(filter, messageContext)){
                    filter.doFilter(messageContext, this);
                } else{
                    this.doFilter(messageContext);
                }
            }

            if (_filter < filters.size()) {
                bl = false;
            }
            return bl;
        }

        private boolean messageTypeCheck(Filter filter, MessageContext messageContext) {
            List<MessageType> types = filter.attentionTypes();
            if (types == null || types.isEmpty()) {
                return true;
            }
            if (types.contains(messageContext.getMessageType())) {
                return true;
            }
            return false;
        }


    }

}

package com.dmsg.message;

import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.exception.AuthenticationException;
import com.dmsg.filter.Filter;
import com.dmsg.filter.FilterChain;
import com.dmsg.message.vo.*;
import com.dmsg.server.DmsgServerContext;
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
    private ChannelId channelId;
    private SourceAddress sourceAddress;
    private DmsgServerContext dmsgServerContext;


    public MessageHandler(MessageContext messageContext) {
        this.messageContext = messageContext;
        message = messageContext.getMessage();
        channelId = messageContext.getChannelHandlerContext().channel().id();
        dmsgServerContext = messageContext.getServerContext();
        filters = dmsgServerContext.getFilters();
        sourceAddress = new SourceAddress();
        sourceAddress.setHost(dmsgServerContext.getHostDetail().getIp());
        sourceAddress.setPort(dmsgServerContext.getHostDetail().getPort());
        channelManager = LocalUserChannelManager.getInstance();
    }

    public void run() {
        try {
            process();
        } catch (Exception e) {
            e.printStackTrace();
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
     * 判断用户鉴权信息
     *
     * @throws AuthenticationException
     */
    private void auth() throws AuthenticationException, CloneNotSupportedException {
        String username = channelManager.findUserByChannelId(channelId);
        if (StringUtils.isNotEmpty(username)) {
            SourceAddress tmp = sourceAddress.clone();
            tmp.setUser(username);
            message.setFrom(tmp);
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

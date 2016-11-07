package com.dmsg.channel;

import com.dmsg.data.UserDetail;
import com.dmsg.server.DmsgServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cjl on 2016/7/11.
 */
public class LocalUserChannelManager {
    final private Map<String, ChannelHandlerContext> sessions = new ConcurrentHashMap<String, ChannelHandlerContext>();
    final private Map<ChannelId, String> relations = new ConcurrentHashMap<ChannelId, String>();
    final private Map<String, String> auths = new ConcurrentHashMap<String, String>();
    private static LocalUserChannelManager instance = new LocalUserChannelManager();
    public static LocalUserChannelManager getInstance(){
        return instance;
    }

    private LocalUserChannelManager() {
    }

    // 增加用户与连接的上下文映射
    public void addContext(String username,String authKey, ChannelHandlerContext ctx) {
        UserDetail userDetail = new UserDetail();
        userDetail.setLastTime(System.currentTimeMillis());
        userDetail.setUserName(username);
        userDetail.setStatus(1);
        userDetail.setLoginHost(DmsgServerContext.getServerContext().getHostDetail());

        synchronized (sessions) {
            sessions.put(username, ctx);
            relations.put(ctx.channel().id(), username);
            auths.put(username, authKey);
        }
    }

    // 获取指定用户的连接上下文
    public ChannelHandlerContext getContext(String name){
        return sessions.get(name);
    }

    // 根据用户名删除session
    public void removeContext(String name){
        sessions.remove(name);
    }

    // 判断指定的用户名当前是否在线
    public boolean isAvailable(String name){
        return sessions.containsKey(name) && (sessions.get(name) != null);
    }

    public String getAuthKeyByUsername(String username) {
        return auths.get(username);
    }

    // 获取所有的用户名
    public synchronized Set<String> getAll(){
        return sessions.keySet();
    }

    // 获取所有连接的上下文对象
    public synchronized Collection<ChannelHandlerContext> getAllClient(){
        return sessions.values();
    }

    // 根据上下文删除用户session
    public void removeContext(ChannelHandlerContext ctx){
        String name = relations.get(ctx.toString());
        if(name != null){
            sessions.remove(name);
            relations.remove(ctx.toString());
        }
    }

    // 统计当前在线人数
    public int staticClients(){
        return relations.size();
    }

    public String findUserByChannelId(ChannelId channelId) {
        return relations.get(channelId);
    }
}

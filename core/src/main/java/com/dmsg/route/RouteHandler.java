package com.dmsg.route;

import com.dmsg.message.MessageContext;

/**
 * Created by cjl on 2016/7/27.
 */
public abstract class RouteHandler {

    private static RouteHandler handler;

    public static RouteHandler getHandler() {
        return handler;
    }

    /**
     *
     * 1.获取用户所在主机
        路由策略
        1.1 广播-缓存策略 广播到其它主机-存在该用户的主机响应-缓存该用户所在的主机在本地缓存(需要设置本地缓存失效时间)。
        1.2 注册-缓存策略 每个用户鉴权时将用户信息注册到共享缓存服务器-查找到用户主机时缓存到本地缓存(需要设置本地缓存失效时间)。

     * 2.如果没有任何主机响应(1.1策略)或存在(1.2策略),则离线缓存消息,定时任务处理
     **
     *
     * @param messageContext 消息上下文
     * @return
     */

    public abstract RouteMessage route(MessageContext messageContext);



}

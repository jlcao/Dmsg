package com.dmsg.cache;

import com.alibaba.fastjson.JSON;
import com.dmsg.channel.RemoteHostChannelManager;
import com.dmsg.data.HostDetail;
import com.dmsg.netty.NetSocketClient;
import com.dmsg.server.DmsgServerConfig;
import com.dmsg.server.DmsgServerContext;
import com.dmsg.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cjl on 2016/7/28.
 */
public class HostCache{
    static Map<String, HostDetail> host = new ConcurrentHashMap<String, HostDetail>();
    CacheManager cacheManager;
    DmsgServerConfig config;
    RemoteHostChannelManager remoteHostChannelManager;
    long refreshTime;
    HostDetail local;
    Logger logger = LoggerFactory.getLogger(HostCache.class);
    private static HostCache hostCache;

    public static HostCache getInstance(DmsgServerContext context) {
        if (hostCache == null) {
            hostCache = new HostCache(context);
            ClientJob job = ClientJob.getInstance(context, hostCache);
        }
        return hostCache;
    }

    private HostCache(DmsgServerContext context) {
        this.cacheManager = context.getCache();
        this.config = context.getConfig();
        this.refreshTime = config.getHostRefreshCycle();

        this.remoteHostChannelManager = context.getRemotHostsChannelManager();
        local = context.getHostDetail();

    }

    public void refresh() throws Exception {
        Map<String, String> hostsMap = cacheManager.getResource().hgetAll(config.getServerNodeFlag());
        for (String name : hostsMap.keySet()) {
            logger.info("name:{}", name);
            HostDetail detail = host.get(name);
            if (detail != null) {
                logger.info("存在host:{} ,超时处理", name);
                if (isTimeOut(detail)) {
                    detail.setLastTime(System.currentTimeMillis());
                }
            } else {
                logger.info("不存在host:{},all:{}", name, host);
                detail = parse(hostsMap.get(name));
                this.connection(detail);
            }
        }
    }


    public void remove(String hostname) {
        synchronized (host) {
            host.remove(hostname);
        }
    }

    public void put(HostDetail hostDetail) {
        synchronized (host) {
            host.put(hostDetail.keyFiled(), hostDetail);
        }
    }

    private void connection(HostDetail hostDetail) throws InterruptedException {
        if (!hostDetail.getIp().equals(local.getIp()) || hostDetail.getPort() != local.getPort()) {
            if (!remoteHostChannelManager.isAvailable(hostDetail.keyFiled())) {
                NetSocketClient client = new NetSocketClient(hostDetail.getIp(), hostDetail.getPort());
                client.connection();
            }
        }
    }

    private HostDetail getHostOnCache(String hostName) {
        String str = cacheManager.getResource().hget(config.getServerNodeFlag(), hostName);
        return parse(str);
    }

    private HostDetail parse(String string) {
        HostDetail detail = null;
        if (!NullUtils.isEmpty(string)) {
            detail = JSON.toJavaObject(JSON.parseObject(string), HostDetail.class);
            detail.setLastTime(System.currentTimeMillis());
        }
        return detail;
    }

    private boolean isTimeOut(HostDetail detail) {
        return System.currentTimeMillis() - detail.getLastTime() > refreshTime ? true : false;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public DmsgServerConfig getConfig() {
        return config;
    }

    public void setConfig(DmsgServerConfig config) {
        this.config = config;
    }


    public Set<HostDetail> getAll() {
        return (Set<HostDetail>) host.values();
    }
}
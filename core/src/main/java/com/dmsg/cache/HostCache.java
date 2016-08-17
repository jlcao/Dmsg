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
public class HostCache {
    final Map<String, HostDetail> host = new ConcurrentHashMap<String, HostDetail>();
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
        }
        return hostCache;
    }

    private HostCache(DmsgServerContext context) {
        this.cacheManager = context.getCache();
        this.config = context.getConfig();
        this.refreshTime = config.getHostRefreshCycle();

        this.remoteHostChannelManager = context.getRemotHostsChannelManager();
        local = context.getHostDetail();


        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true) {
                    try {
                        refresh();
                        logger.info("远程服务器缓存刷新成功！");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();
    }

    private void refresh() throws InterruptedException {
        Map<String, String> hostsMap = cacheManager.getResource().hgetAll(config.getServerNodeFlag());
        for (String name : hostsMap.keySet()) {
            HostDetail detail = host.get(name);
            if (detail != null) {
                if (isTimeOut(detail)) {
                    detail.setLastTime(System.currentTimeMillis());
                }
            } else {
                detail = parse(hostsMap.get(name));
                this.put(detail);
            }
        }
    }





    public void remove(String hostname) {
        synchronized (host) {
            host.remove(hostname);
        }
    }

    private void put(HostDetail hostDetail) throws InterruptedException {
        if (!(hostDetail.getIp().equals(local.getIp()) && hostDetail.getPort() == local.getPort())) {
            NetSocketClient client = new NetSocketClient(hostDetail.getIp(), hostDetail.getPort());
            client.connection();
        }
        if (hostDetail != null) {
            synchronized (host) {
                host.put(hostDetail.getIp() + ":" + hostDetail.getPort(), hostDetail);
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

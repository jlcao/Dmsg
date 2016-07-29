package com.dmsg.cache;

import com.alibaba.fastjson.JSON;
import com.dmsg.data.HostDetail;
import com.dmsg.server.DmsgServerConfig;
import com.dmsg.server.DmsgServerContext;
import com.dmsg.utils.NullUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cjl on 2016/7/28.
 */
public class HostCache {
    final Map<String, HostDetail> host = new ConcurrentHashMap<String, HostDetail>();
    CacheManager cacheManager;
    DmsgServerConfig config;
    long refreshTime;

    public HostCache(DmsgServerContext context) {
        this.cacheManager = context.getCache();
        this.config = context.getConfig();
        this.refreshTime = config.getHostRefreshCycle();
    }


    public HostDetail getHost(String hostName) {
        cacheManager = DmsgServerContext.getServerContext().getCache();
        HostDetail detail = host.get(hostName);
        if (detail == null) {
            detail = getHostOnCache(hostName);
            if (detail != null) {
                synchronized (host) {
                    host.put(hostName, detail);
                }
            }
        } else {
            if (isTimeOut(detail)) {
                detail = getHostOnCache(hostName);
                if (detail != null) {
                    synchronized (host) {
                        host.put(hostName, detail);
                    }
                }
            } else {
                synchronized (host) {
                    host.remove(hostName);
                }
            }
        }
        return detail;
    }

    private HostDetail getHostOnCache(String hostName) {
        HostDetail detail = null;
        String str = cacheManager.getResource().hget(config.getServerNodeFlag(), hostName);
        if (!NullUtils.isEmpty(str)) {
            detail = JSON.toJavaObject(JSON.parseObject(str), HostDetail.class);
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



}

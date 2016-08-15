package com.dmsg.cache;

import com.alibaba.fastjson.JSON;
import com.dmsg.data.HostDetail;
import com.dmsg.server.DmsgServerConfig;
import com.dmsg.server.DmsgServerContext;
import com.dmsg.utils.NullUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cjl on 2016/7/28.
 */
public class HostCache {
    final Map<String, HostDetail> host = new ConcurrentHashMap<String, HostDetail>();
    CacheManager cacheManager;
    DmsgServerConfig config;
    long refreshTime;
    Set<HostDetail> all;


    public HostCache(DmsgServerContext context) {
        this.cacheManager = context.getCache();
        this.config = context.getConfig();
        this.refreshTime = config.getHostRefreshCycle();
        this.all = new HashSet<HostDetail>();
    }


    public HostDetail getHost(String hostName) {
        cacheManager = DmsgServerContext.getServerContext().getCache();
        HostDetail detail = host.get(hostName);
        if (detail == null) {
            detail = getHostOnCache(hostName);

            this.put(detail);
        } else {
            if (isTimeOut(detail)) {
                detail = getHostOnCache(hostName);
                if (detail != null) {
                    this.put(detail);
                } else {
                    this.remove(hostName);
                }
            }
        }
        return detail;
    }

    private void remove(String hostname) {
        synchronized (host) {
            host.remove(hostname);
        }
    }

    private void put(HostDetail hostDetail) {
        if (hostDetail != null) {
            synchronized (host) {
                host.put(hostDetail.getIp() + ":" + hostDetail.getPort(), hostDetail);
            }
            synchronized (all) {
                all.add(hostDetail);
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

        if (all == null) {
            all = new HashSet<HostDetail>();
            Map<String, String> hostsMap = cacheManager.getResource().hgetAll(config.getServerNodeFlag());
            for (String name : hostsMap.keySet()) {
                HostDetail detail = parse(hostsMap.get(name));
                all.add(detail);
                this.put(detail);
            }
        }

        return all;
    }

}

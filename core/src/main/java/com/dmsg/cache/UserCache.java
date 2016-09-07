package com.dmsg.cache;

import com.dmsg.data.UserDetail;
import com.dmsg.server.DmsgServerConfig;
import com.dmsg.server.DmsgServerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cjl on 2016/7/28.
 */
public class UserCache  {
    private static Map<String, UserDetail> users = new ConcurrentHashMap<String, UserDetail>();
    private CacheManager cacheManager;
    private DmsgServerConfig config;
    long refreshTime;
    private static UserCache userCache;
    private UserCache(DmsgServerContext context) {
        this.cacheManager = context.getCache();
        this.config = context.getConfig();
        this.refreshTime = config.getHostRefreshCycle();
    }

    public static UserCache getInstance(DmsgServerContext context) {
        if (userCache == null) {
            userCache = new UserCache(context);
        }
        return userCache;
    }

    public UserDetail getUserByName(String userName) {
        UserDetail detail = users.get(userName);
        if (detail == null) {
            detail = getUserOnCache(userName);
            if (detail != null) {
                synchronized (users) {
                    users.put(userName, detail);
                }
            }
        } else {
            if (isTimeOut(detail)) {
                detail = getUserOnCache(userName);
                if (detail != null) {
                    synchronized (users) {
                        users.put(userName, detail);
                    }
                } else {
                    synchronized (users) {
                        users.remove(userName);
                    }
                }
            }
        }
        return detail;
    }

    private boolean isTimeOut(UserDetail detail) {
        return System.currentTimeMillis() - detail.getLastTime() > refreshTime ? true : false;
    }

    private UserDetail getUserOnCache(String userName) {
        UserDetail userDetail = cacheManager.getUserByName(config.getUserNodeFlag(),userName);
        if (userDetail != null) {
            userDetail.setLastTime(System.currentTimeMillis());
        }
        return userDetail;
    }


    public void remove(String name) {
        synchronized (users) {
            users.remove(name);
        }
    }

    public void put(UserDetail userDetail) {
        synchronized (users) {
            users.put(userDetail.getUserName(), userDetail);
        }
    }

    public boolean contains(String user) {
        return users.containsKey(user);
    }
}

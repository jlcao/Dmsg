package com.dmsg.server;

import com.dmsg.exception.ServerConfigException;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by cjl on 2016/7/15.
 */
public class DmsgServerConfig{
    private static Properties properties = new Properties();
    private int port;
    private String host;
    private int maxRet;

    public DmsgServerConfig() {
        try {
            properties.load(new FileInputStream("core/src/main/resource/cfg.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DmsgServerConfig(String configPath) {
        if (configPath == null) {
            configPath = "core/src/main/resource/cfg.properties";
        }
        try {
            properties.load(new FileInputStream(configPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHost(String host) throws ServerConfigException {
        if (StringUtils.isEmpty(host)) {
            throw new ServerConfigException("set Host is null");
        }
        this.host = host;
    }
    public void setPort(int port) {
        this.port = port;
    }


    public int getPort() {
        String port = properties.getProperty("server.port","3710");
        return new Integer(port);
    }

    public String getHost() {
        String host = properties.getProperty("server.host", "localhost");
        return host;
    }

    public int getCachePort() {
        String port = properties.getProperty("redis.port", "6379");
        return Integer.parseInt(port);
    }
    public String getCacheHost() {
        String host = properties.getProperty("redis.host", "localhost");
        return host;
    }

    public String getProtocol() {
        return properties.getProperty("trans.protocol", "websocket");
    }

    public String getServerNodeFlag() {
        return properties.getProperty("cache.node.key", "dmsg.nodes");
    }

    public String getAgentIp() {
        return properties.getProperty("agent.ip", "");
    }
    public String getAgentPort() {
        return properties.getProperty("agent.port", "");
    }

    public long getHostRefreshCycle() {
        String minute = properties.getProperty("node.refresh.cycle", "3");
        return Integer.parseInt(minute) * 1000L * 60;
    }

    public String getUserNodeFlag() {
        return properties.getProperty("cache.user.key", "dmsg.usercache");
    }

    public String getServerAuthKey() {
        return properties.getProperty("server.auth.key", "dmsg");
    }

    public int getMaxRet() {
        String str = properties.getProperty("max.ret", "10");
        return Integer.parseInt(str);
    }

}

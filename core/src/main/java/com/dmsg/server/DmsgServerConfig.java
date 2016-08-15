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
    public DmsgServerConfig() {
        try {
            properties.load(new FileInputStream("D:\\E\\git\\Dmsg\\core\\src\\main\\resource\\cfg.properties"));
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
        String port = properties.getProperty("port","3710");
        return new Integer(port);
    }

    public String getHost() {
        String host = properties.getProperty("post", "localhost");
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
        String protocol = properties.getProperty("trans.protocol", "websocket");
        return null;
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

        return Integer.parseInt(minute) * 1000 * 60;
    }

    public String getUserNodeFlag() {
        return properties.getProperty("cache.user.key", "dmsg.usercache");
    }

    public String getServerAuthKey() {
        return properties.getProperty("server.auth.key", "dmsg");
    }
}

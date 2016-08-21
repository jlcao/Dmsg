package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/8/12.
 */
public class SourceAddress implements Cloneable  {
    private String host;
    private int port;
    private String user;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public SourceAddress clone() {
        try {
            return (SourceAddress) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }

    }

    @Override
    public String toString() {
        return "SourceAddress{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                '}';
    }
}

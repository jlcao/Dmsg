package com.dmsg.data;

/**
 * Created by cjl on 2016/7/28.
 */
public class HostDetail{
    private String ip;
    private int port;
    private long lastTime;
    private long hostId;
    private long userSize;
    private long msgSize;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public long getUserSize() {
        return userSize;
    }

    public void setUserSize(long userSize) {
        this.userSize = userSize;
    }

    public long getMsgSize() {
        return msgSize;
    }

    public void setMsgSize(long msgSize) {
        this.msgSize = msgSize;
    }


}

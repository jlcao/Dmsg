package com.dmsg.data;

/**
 * Created by cjl on 2016/7/28.
 */
public class UserDetail {
    private String userName;
    private HostDetail loginHost;
    private String msgSize;
    private int status;  //1:上线  2:离线
    private long lastTime;


    public int getStatus() {
        return status;
    }

    public String getMsgSize() {
        return msgSize;
    }

    public void setMsgSize(String msgSize) {
        this.msgSize = msgSize;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public HostDetail getLoginHost() {
        return loginHost;
    }

    public void setLoginHost(HostDetail loginHost) {
        this.loginHost = loginHost;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }


    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result;
        return result;
    }
}

package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public class AuthReqMessage extends MessageBody {

    private String username;
    private String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}

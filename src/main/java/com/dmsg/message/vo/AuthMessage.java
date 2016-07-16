package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/7/11.
 */
public class AuthMessage extends MessageBase {
    private String username;
    private String password;

    public AuthMessage() {
        super(MessageType.AUTH.getCode());
    }

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

    @Override
    public Object getBody() {
        return this;
    }

    @Override
    public String toString() {
        return "AuthMessage{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

package com.dmsg.message.vo;

/**
 * Created by cjl on 2016/8/12.
 */
public class DestAddress {
    private String[] users;

    public DestAddress(String... users) {
        this.users = users;
    }

    public DestAddress() {
    }

    public String[] getUsers() {
        return users;
    }

    public void setUsers(String... users) {
        this.users = users;
    }
}

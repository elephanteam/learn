package com.limit.learn.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserBean extends LoginBean{

    private String token;

    @Generated(hash = 330548116)
    public UserBean(String token) {
        this.token = token;
    }

    @Generated(hash = 1203313951)
    public UserBean() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

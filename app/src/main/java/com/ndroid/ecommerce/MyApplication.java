package com.ndroid.ecommerce;

import android.app.Application;

import model.user.UserInfo;

public class MyApplication extends Application {
    private UserInfo userInfo;
    private String username;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

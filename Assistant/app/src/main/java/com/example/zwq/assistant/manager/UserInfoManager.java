package com.example.zwq.assistant.manager;

import com.example.zwq.assistant.been.User;

public class UserInfoManager {

    private User loginUser;
    private String spName = "loginUser";

    private static class Holder {
        private static UserInfoManager userInfoManager = new UserInfoManager();
    }

    private UserInfoManager() {
    }

    public static UserInfoManager getInstance() {
        return Holder.userInfoManager;
    }


    public User getLoginUser(){
        return loginUser;
    }

    public int getUid(){
        if(loginUser!=null){
            return loginUser.getId();
        }
        return 0;
    }

    public String getUserToken(){
        if(loginUser!=null){
            return loginUser.getToken();
        }
        return "";
    }

    public void onLogin(User loginUser) {
        refreshUserInfo(loginUser);
    }

    public void init() {
    }

    public void onLoginOut() {
        loginUser = null;

    }

    public void refreshUserInfo(User loginUser) {
        this.loginUser = loginUser;

    }

}

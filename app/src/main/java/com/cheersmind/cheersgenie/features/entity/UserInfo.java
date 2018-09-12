package com.cheersmind.cheersgenie.features.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 用户信息
 */
public class UserInfo {

    //    {
//        "user_id":123,          //用户ID
//            "user_name":"string",   //用户名
//            "sex":1,                //性别，0未知，1男，2女
//            "avatar":"url"          //用户头像URL
//    }

    @InjectMap(name = "user_id")
    private String userId;

    @InjectMap(name = "user_name")
    private String userName;

    @InjectMap(name = "sex")
    private int sex;

    @InjectMap(name = "avatar")
    private String avatar;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 用户（作者、评论者等）
 */
public class UserEntity extends DataSupport implements Serializable {

    //ID
    @InjectMap(name = "user_id")
    private String userId;

    //用户名
    @InjectMap(name = "user_name")
    private String userName;

    //头像
    @InjectMap(name = "avatar")
    private String avatar;

    //昵称
    @InjectMap(name = "nick_name")
    private String nickName;


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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}

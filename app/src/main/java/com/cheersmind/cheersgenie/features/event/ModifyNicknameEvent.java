package com.cheersmind.cheersgenie.features.event;

import com.cheersmind.cheersgenie.features.entity.UserInfo;

/**
 * 修改昵称事件
 */
public class ModifyNicknameEvent {

    //用户信息
    private UserInfo userInfo;

    public ModifyNicknameEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

}


package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 第三方平台账号
 */
public class ThirdPlatformAccount implements Serializable {

    //昵称
    @InjectMap(name = "nickname")
    private String nickname;

    //平台（qq、weixin）
    @InjectMap(name = "plat_source")
    private String plat_source;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPlat_source() {
        return plat_source;
    }

    public void setPlat_source(String plat_source) {
        this.plat_source = plat_source;
    }
}

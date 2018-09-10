package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * QQ token对象
 */
public class QQTokenEntity extends DataSupport implements Serializable{

    @InjectMap(name = "access_token")
    private String accessToken;

    @InjectMap(name = "expires_in")
    private String expiresIn;

//    @InjectMap(name = "refresh_token")
//    private String refreshToken;

    @InjectMap(name = "openid")
    private String openid;

//    @InjectMap(name = "scope")
//    private String scope;

//    @InjectMap(name = "unionid")
//    private String unionid;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

}

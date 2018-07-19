package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/14.
 */

public class WXUserInfoEntity extends DataSupport implements Serializable {

//    {
//        "user_id":123,                  //用户编号ID
//            "access_token" : "string" ,            //Token
//            "expires_at" : "2015-06-30T14:17:21.275+0800",   //token过期时间
//            "refresh_token" : "string" ,          //刷新用Token
//            "mac_key" : "string" ,               //Mac key值
//            "mac_algorithm" : "hmac-sha-256" ,         //Mac算法
//            "server_time": "2015-06-30T14:17:21.275+0800"   //服务器时间
//    }

    @InjectMap(name = "user_id")
    private long userId;

    @InjectMap(name = "access_token")
    private String accessToken;

    @InjectMap(name = "expires_at")
    private String expiresAt;

    @InjectMap(name = "refresh_token")
    private String refreshToken;

//    @InjectMap(name = "mac_key")
    @InjectMap(name = "code")
    private String macKey;

    @InjectMap(name = "mac_algorithm")
    private String macAlgorithm;

    @InjectMap(name = "server_time")
    private String serverTime;

    private long sysTimeMill;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getMacKey() {
        return macKey;
    }

    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }

    public String getMacAlgorithm() {
        return macAlgorithm;
    }

    public void setMacAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public long getSysTimeMill() {
        return sysTimeMill;
    }

    public void setSysTimeMill(long sysTimeMill) {
        this.sysTimeMill = sysTimeMill;
    }
}

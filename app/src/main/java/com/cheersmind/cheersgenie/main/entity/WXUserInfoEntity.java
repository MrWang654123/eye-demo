package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/14.
 */

public class WXUserInfoEntity extends DataSupport implements Serializable {

//    {
//        "user_id":123,                                     //用户编号ID
//            "access_token" : "string" ,                         //Token
//            "expires_in" : "2015-06-30T14:17:21.275+0800",        //token过期时间【修改命名为标准oauth2】
//            "refresh_token" : "string" ,                         //刷新用Token
//            "code" : "string" ,                                  //Mac key值【改为code】
//            "server_time": "2015-06-30T14:17:21.275+0800",         //服务器时间
//            "bind_mobile":"true"                                //是否绑定手机号，客户端可根据这个字段判断是否要让用户绑定手机号
//    }

    //用户ID
    @InjectMap(name = "user_id")
    private long userId;

    //访问token
    @InjectMap(name = "access_token")
    private String accessToken;

    //token过期时间【修改命名为标准oauth2】
//    @InjectMap(name = "expires_at")
    @InjectMap(name = "expires_in")
    private String expiresAt;

    //刷新用Token
    @InjectMap(name = "refresh_token")
    private String refreshToken;

    //加密用的key
//    @InjectMap(name = "mac_key")
    @InjectMap(name = "code")
    private String macKey;

    //服务器时间
    @InjectMap(name = "server_time")
    private String serverTime;

    //是否绑定手机号，客户端可根据这个字段判断是否要让用户绑定手机号
    @InjectMap(name = "bind_mobile")
    private boolean bindMobile;

    //本地时间
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

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public boolean isBindMobile() {
        return bindMobile;
    }

    public void setBindMobile(boolean bindMobile) {
        this.bindMobile = bindMobile;
    }

    public long getSysTimeMill() {
        return sysTimeMill;
    }

    public void setSysTimeMill(long sysTimeMill) {
        this.sysTimeMill = sysTimeMill;
    }
}

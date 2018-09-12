package com.cheersmind.cheersgenie.features.dto;

/**
 *  创建会话 Dto
 */
public class CreateSessionDto {

    //会话类型，0：注册(手机)，1：登录(帐号、密码登录)，2：手机找回密码，3：登录(短信登录)，4:下发短信验证码
    private int sessionType;

    //租户名称
    private String tenant;

    //设备唯一ID
    private String deviceId;


    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}

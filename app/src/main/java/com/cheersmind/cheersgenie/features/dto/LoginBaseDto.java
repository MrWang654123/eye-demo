package com.cheersmind.cheersgenie.features.dto;

import android.os.Build;

import java.io.Serializable;

/**
 * 登录dto的通用数据
 */
public class LoginBaseDto implements Serializable {

    //租户名称
    private String tenant;

    //登录设备类型，如：ios\android\browser
    private String deviceType = "android";

    //登录设备机器型号，如：小米6，苹果8
    private String deviceDesc = Build.MODEL;

    //设备唯一ID
    private String deviceId;


    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceDesc() {
        return deviceDesc;
    }

    public void setDeviceDesc(String deviceDesc) {
        this.deviceDesc = deviceDesc;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

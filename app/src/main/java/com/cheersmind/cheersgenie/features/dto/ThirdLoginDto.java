package com.cheersmind.cheersgenie.features.dto;


/**
 * 第三方登录的请求数据
 */
public class ThirdLoginDto extends LoginBaseDto {

    //第三方平台ID
    private String openId;

    //平台名称
    private String platSource;

    //第三方平台token
    private String thirdAccessToken;

    //我们的应用第三方平台注册的ID
    private String appId;


    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getPlatSource() {
        return platSource;
    }

    public void setPlatSource(String platSource) {
        this.platSource = platSource;
    }

    public String getThirdAccessToken() {
        return thirdAccessToken;
    }

    public void setThirdAccessToken(String thirdAccessToken) {
        this.thirdAccessToken = thirdAccessToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}

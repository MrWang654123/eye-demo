package com.cheersmind.cheersgenie.features.dto;

/**
 * 第三方平台绑定dto
 */
public class ThirdPlatBindDto {

    //第三方平台给用户分配的Id,如微信、QQ的openId
    private String openId;

    //第三方登录来源，目前支持：1-qq 2-weixin
    private String platSource;

    //QQ平台必填（我们的应用在第三方平台注册的ID）
    private String appId;

    //第三方的access_token
    private String thirdAccessToken;

    //租户名
    private String tenant;

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getThirdAccessToken() {
        return thirdAccessToken;
    }

    public void setThirdAccessToken(String thirdAccessToken) {
        this.thirdAccessToken = thirdAccessToken;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}

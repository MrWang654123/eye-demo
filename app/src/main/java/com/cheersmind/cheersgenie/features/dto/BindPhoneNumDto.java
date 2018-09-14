package com.cheersmind.cheersgenie.features.dto;

/**
 * 绑定手机号dto
 */
public class BindPhoneNumDto {

    //手机号(必填)，检验手机的合法性
    private String mobile;

    //短信验证码（必填）
    private String mobileCode;

    //租户名（必填）
    private String tenant;

    //手机国际区号(选填)，中国：+86（默认）
    private String areaCode;

    //会话ID（必填）
    private String sessionId;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(String mobileCode) {
        this.mobileCode = mobileCode;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

package com.cheersmind.cheersgenie.features.dto;

/**
 * 手机号登录的dto
 */
public class PhoneNumLoginDto extends LoginBaseDto {

    //手机号
    private String mobile;

    //短信验证码
    private String mobile_code;

    //会话ID
    private String sessionId;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile_code() {
        return mobile_code;
    }

    public void setMobile_code(String mobile_code) {
        this.mobile_code = mobile_code;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

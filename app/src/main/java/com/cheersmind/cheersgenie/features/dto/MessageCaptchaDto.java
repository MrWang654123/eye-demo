package com.cheersmind.cheersgenie.features.dto;

/**
 * 短信验证码dto
 */
public class MessageCaptchaDto {

    //手机号(必填)，检验手机的合法性
    private String mobile;

    //短信业务类型(必填)：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
    private int type;

    //租户名（必填）
    private String tenant;

    //手机区号
    private String areaCode;

    //会话ID（必填）
    private String sessionId;

    //图形验证码
    private String imageCaptcha;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getImageCaptcha() {
        return imageCaptcha;
    }

    public void setImageCaptcha(String imageCaptcha) {
        this.imageCaptcha = imageCaptcha;
    }
}

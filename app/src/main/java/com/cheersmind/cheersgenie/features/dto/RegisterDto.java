package com.cheersmind.cheersgenie.features.dto;

/**
 * 账号注册dto
 */
public class RegisterDto {

    //手机号
    private String mobile;

    //短信验证码
    private String mobileCode;

    //密码(需要加密)
    private String password;

    //手机区号
    private String areaCode;

    //租户名（必填）
    private String tenant;

    //第三方平台登录的dto
    private ThirdLoginDto thirdLoginDto;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public ThirdLoginDto getThirdLoginDto() {
        return thirdLoginDto;
    }

    public void setThirdLoginDto(ThirdLoginDto thirdLoginDto) {
        this.thirdLoginDto = thirdLoginDto;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}

package com.cheersmind.cheersgenie.features.dto;

/**
 * 账号登录dto
 */
public class AccountLoginDto extends LoginBaseDto {

    //用户名或手机号
    private String account;
    //密码(需要加密)
    private String password;
    //会话ID
    private String sessionId;
    //验证码
    private String verificationCode;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}

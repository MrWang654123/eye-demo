package com.cheersmind.cheersgenie.features.dto;

/**
 * 重置密码的dto
 */
public class ResetPasswordDto {

    //手机号
    private String mobile;

    //短信验证码
    private String mobile_code;

    //租户名称
    private String tenant;

    //新密码
    private String new_password;

    //手机国际区号(选填)，中国：+86（默认）
    private String area_code;

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

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }
}

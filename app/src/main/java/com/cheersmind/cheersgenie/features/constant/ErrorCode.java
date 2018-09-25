package com.cheersmind.cheersgenie.features.constant;

/**
 * 错误码
 */
public class ErrorCode {

    //无效的授权令牌
    public static final String AC_AUTH_INVALID_TOKEN ="AC_AUTH_INVALID_TOKEN";

    //账号不存在（账号登录）
    public static final String AC_ACCOUNT_NOT_EXIST  = "AC_ACCOUNT_NOT_EXIST";
    //账号不存在（第三方平台登录）
    public static final String AC_THIRD_ACCOUNT_NOT_EXIST = "AC_THIRD_ACCOUNT_NOT_EXIST";
    //账号不存在（手机短信登录）
    public static final String AC_USER_NOT_EXIST = "AC_USER_NOT_EXIST";

    //手机号已注册
    public static final String AC_PHONE_HAS_REGISTER = "AC_PHONE_HAS_REGISTER";
    //账号登录：密码不正确
    public static final String AC_WRONG_PASSWORD = "AC_WRONG_PASSWORD";

    //Session 未创建或已过期
    public static final String AC_SESSION_EXPIRED = "AC_SESSION_EXPIRED";
    //Session无效
    public static final String AC_SESSION_INVALID = "AC_SESSION_INVALID";
    //需要图形验证码
    public static final String AC_IDENTIFY_CODE_REQUIRED = "AC_IDENTIFY_CODE_REQUIRED";
    //无效的图形验证码
    public static final String AC_IDENTIFY_CODE_INVALID = "AC_IDENTIFY_CODE_INVALID";

    //短信验证码无效
    public static final String AC_SMS_INVALID = "AC_SMS_INVALID";

    //您的今天短信验证码输入错误次数已超过上限
    public static final String AC_SMSCODE_ERROR_OVER_SUM = "AC_SMSCODE_ERROR_OVER_SUM";


}

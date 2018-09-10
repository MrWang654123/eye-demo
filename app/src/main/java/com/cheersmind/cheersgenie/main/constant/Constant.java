package com.cheersmind.cheersgenie.main.constant;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

/**
 * Created by goodm on 2017/4/15.
 */
public class Constant {

    public static int VERSION_FEATURE = 11;
//    public static final String API_APP_ID = "b3575040-17ba-47a3-8bb9-357f2a928582";
    public static final String API_APP_ID = "3b8d2363-9ef0-11e8-b880-161768d3f948";

    //反馈
    public static final String FEEDBACK_APP_KEY = "25035344";
    public static final String FEEDBACK_APP_SECRET = "1b1b8a41c7f29936fd735ac8bf0cc8d2";

    //统计
    public static final String UAPP_KEY = "5a2bbbec8f4a9d7ac10001da";
//    public static final String UAPP_KEY = "593e99fe677baa7c00001499";

    //微信登入
    public static final String WX_APP_ID = "wxe8a3494ba5607e3f";
    public static IWXAPI wx_api;
    public static final String WX_APP_SECTET = "167c25dbd7e3aab8a0927e92539dd774";

//    public static String CUR_EXAM_ID = "6";

    //QQ登录
    public static final String QQ_APP_ID = "1107697503";
    public static Tencent mTencent;

}

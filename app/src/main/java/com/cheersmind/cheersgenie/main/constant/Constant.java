package com.cheersmind.cheersgenie.main.constant;

import com.baidu.tts.client.TtsMode;
import com.cheersmind.cheersgenie.features.utils.baidu.OfflineResource;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

/**
 * Created by goodm on 2017/4/15.
 */
public class Constant {

    public static int VERSION_FEATURE = 1;
//    public static final String API_APP_ID = "b3575040-17ba-47a3-8bb9-357f2a928582";
    public static final String API_APP_ID = "3b8d2363-9ef0-11e8-b880-161768d3f948";

    //反馈
    public static final String FEEDBACK_APP_KEY = "25035344";
    public static final String FEEDBACK_APP_SECRET = "1b1b8a41c7f29936fd735ac8bf0cc8d2";

    //友盟统计
    public static final String UAPP_KEY = "5bd18dadf1f556c16000000d";
//    public static final String UAPP_KEY = "593e99fe677baa7c00001499";

    //微信登入
    public static final String WX_APP_ID = "wxe8a3494ba5607e3f";
    public static IWXAPI wx_api;
    public static final String WX_APP_SECTET = "167c25dbd7e3aab8a0927e92539dd774";

//    public static String CUR_EXAM_ID = "6";

    //QQ登录
    public static final String QQ_APP_ID = "1107697503";
    public static Tencent mTencent;

    //百度语音
    public static final String BAIDU_APP_ID = "14794450";
    public static final String BAIDU_APP_KEY = "uztB1Hd07a3N5X4E2x5rz6vf";
    public static final String BAIDU_SECRET_KEY = "y2Yki6mIWHpR1N3enrWbdEwiuNGS8Z5o";
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    public static final TtsMode ttsMode = TtsMode.MIX;
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    public static final String offlineVoice = OfflineResource.VOICE_DUYY;

}

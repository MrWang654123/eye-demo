package com.cheersmind.cheersgenie.module.login;

/**
 * Created by Administrator on 2018/7/24.
 */

public class EnvHostManager {
    //生产环境
    public static final String UC_HOST_PRODUCT  = "http://psytest-server.cheersmind.com";
    public static final String API_HOST_PRODUCT = "http://psytest-server.cheersmind.com";
    public static final String WEB_HOST_PRODUCT = "http://psytest-web.cheersmind.com";

    //生产环境灰度发布时的B环境
    public static final String UC_HOST_PRODUCT_B  = "http://s02.psytest-server.cheersmind.com";
    public static final String API_HOST_PRODUCT_B = "http://s02.psytest-server.cheersmind.com";
    public static final String WEB_HOST_PRODUCT_B = "http://s02.psytest-web.cheersmind.com";

    //测试环境
    public static final String UC_HOST_DEVELOP  = "http://psytest-server.test.cheersmind.qst";
    public static final String API_HOST_DEVELOP = "http://psytest-server.test.cheersmind.qst";
    public static final String WEB_HOST_DEVELOP = "http://psytest-web.test.cheersmind.qst";

    //本地其他机器测试环境
    public static final String UC_HOST_LOCAL_OTHER  = "http://192.168.205.202:8080";
    public static final String API_HOST_LOCAL_OTHER = "http://192.168.205.202:8080";
    public static final String WEB_HOST_LOCAL_OTHER = "http://192.168.205.202:8080";

    //开发环境
    public static final String UC_HOST_LOCAL  = "http://127.0.0.1:8080";
    public static final String API_HOST_LOCAL = "http://127.0.0.1:8080";
    public static final String WEB_HOST_LOCAL = "http://psytest-web.test.cheersmind.qst";

    private String ucHost;
    private String apiHost;
    private String webHost;


//    //开发环境
//    public static final String VIDEO_SIGN_KEY_DEVELOP = "b7ab98891198478b0f897dabebbb62aaaaa8ba8a";
//    //生产环境
//    public static final String VIDEO_SIGN_KEY_PRODUCT = "42a8d9aadd8bc899c499fa985eb9cf9ae7bbd3b8";
    //开发环境
    public static final String VIDEO_SIGN_KEY_DEVELOP = "3deaadbc1e5540539884401ae0abaf0b";
    //生产环境
    public static final String VIDEO_SIGN_KEY_PRODUCT = "3deaadbc1e5540539884401ae0abaf0b";

    //视频签名key
    private String videoSignKey;

    private static EnvHostManager instance;
    private EnvHostManager (){}

    public static EnvHostManager getInstance() {
        if (instance == null) {
            instance = new EnvHostManager();
        }
        return instance;
    }

    public String getUcHost() {
        return ucHost;
    }

    public void setUcHost(String ucHost) {
        this.ucHost = ucHost;
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public String getWebHost() {
        return webHost;
    }

    public void setWebHost(String webHost) {
        this.webHost = webHost;
    }

    public String getVideoSignKey() {
        return videoSignKey;
    }

    public void setVideoSignKey(String videoSignKey) {
        this.videoSignKey = videoSignKey;
    }
}

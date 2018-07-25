package com.cheersmind.smartbrain.module.login;

/**
 * Created by Administrator on 2018/7/24.
 */

public class EnvHostManager {
    //生产环境
    public static final String UC_HOST_PRODUCT = "http://psytest-server.cheersmind.com";
    public static final String API_HOST_PRODUCT = "http://psytest-server.cheersmind.com";
    public static final String WEB_HOST_PRODUCT = "http://psytest-web.cheersmind.com";

    //测试环境
    public static final String UC_HOST_DEVELOP = "http://psytest-server.test.cheersmind.qst";
    public static final String API_HOST_DEVELOP = "http://psytest-server.test.cheersmind.qst";
    public static final String WEB_HOST_DEVELOP = "http://psytest-web.test.cheersmind.qst";

    //开发环境
    public static final String UC_HOST_LOCAL = "http://127.0.0.1:8080";
    public static final String API_HOST_LOCAL = "http://127.0.0.1:8080";
    public static final String WEB_HOST_LOCAL = "http://psytest-web.test.cheersmind.qst";

    private String ucHost;
    private String apiHost;
    private String webHost;

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
}

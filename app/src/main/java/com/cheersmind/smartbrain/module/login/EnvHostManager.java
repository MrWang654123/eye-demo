package com.cheersmind.smartbrain.module.login;

/**
 * Created by Administrator on 2018/7/24.
 */

public class EnvHostManager {

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

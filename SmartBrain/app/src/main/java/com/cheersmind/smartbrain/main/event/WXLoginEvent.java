package com.cheersmind.smartbrain.main.event;

/**
 * Created by Administrator on 2018/4/17.
 */

public class WXLoginEvent {

    private String code;

    public WXLoginEvent(String code){
        setCode(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

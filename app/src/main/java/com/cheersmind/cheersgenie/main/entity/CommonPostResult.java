package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * Created by Administrator on 2017/12/9.
 */

public class CommonPostResult {

    @InjectMap(name = "result")
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}

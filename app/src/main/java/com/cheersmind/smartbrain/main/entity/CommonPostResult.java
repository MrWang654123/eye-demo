package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

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

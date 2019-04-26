package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 第三方平台账号响应的根对象
 */
public class ThirdPlatformAccountRoot {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<ThirdPlatformAccount> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ThirdPlatformAccount> getItems() {
        return items;
    }

    public void setItems(List<ThirdPlatformAccount> items) {
        this.items = items;
    }
}

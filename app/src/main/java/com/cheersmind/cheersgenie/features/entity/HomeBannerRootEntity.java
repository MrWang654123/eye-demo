package com.cheersmind.cheersgenie.features.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 首页Banner响应的根对象
 */
public class HomeBannerRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<HomeBanner> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<HomeBanner> getItems() {
        return items;
    }

    public void setItems(List<HomeBanner> items) {
        this.items = items;
    }
}

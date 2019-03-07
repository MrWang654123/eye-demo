package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 我的勋章列表响应的根对象
 */
public class MedalRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<MedalEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<MedalEntity> getItems() {
        return items;
    }

    public void setItems(List<MedalEntity> items) {
        this.items = items;
    }
}

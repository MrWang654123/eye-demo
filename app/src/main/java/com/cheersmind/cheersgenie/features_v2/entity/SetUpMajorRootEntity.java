package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 开设专业的响应根对象
 */
public class SetUpMajorRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<MajorItem> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<MajorItem> getItems() {
        return items;
    }

    public void setItems(List<MajorItem> items) {
        this.items = items;
    }
}

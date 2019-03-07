package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 专业列表响应的根对象
 */
public class MajorRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<MajorEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<MajorEntity> getItems() {
        return items;
    }

    public void setItems(List<MajorEntity> items) {
        this.items = items;
    }
}

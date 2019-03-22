package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 专业树响应的根对象
 */
public class MajorTreeRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<MajorSubject> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<MajorSubject> getItems() {
        return items;
    }

    public void setItems(List<MajorSubject> items) {
        this.items = items;
    }
}

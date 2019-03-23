package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 重点学科响应的根对象
 */
public class KeySubjectRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<KeySubject> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<KeySubject> getItems() {
        return items;
    }

    public void setItems(List<KeySubject> items) {
        this.items = items;
    }
}

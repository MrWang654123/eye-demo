package com.cheersmind.cheersgenie.features.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 专题的根对象
 */
public class SeminarRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<SeminarEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SeminarEntity> getItems() {
        return items;
    }

    public void setItems(List<SeminarEntity> items) {
        this.items = items;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 开设院校响应的根对象
 */
public class SetupCollegeRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<CollegeEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CollegeEntity> getItems() {
        return items;
    }

    public void setItems(List<CollegeEntity> items) {
        this.items = items;
    }
}

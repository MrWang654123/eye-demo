package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 大学招生年份响应的根对象
 */
public class CollegeEnrollYearRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<CollegeEnrollYear> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CollegeEnrollYear> getItems() {
        return items;
    }

    public void setItems(List<CollegeEnrollYear> items) {
        this.items = items;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 学科组合列表响应的根对象
 */
public class CourseGroupRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<CourseGroup> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CourseGroup> getItems() {
        return items;
    }

    public void setItems(List<CourseGroup> items) {
        this.items = items;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 确认选课响应的根对象
 */
public class ChooseCourseRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<ChooseCourseEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ChooseCourseEntity> getItems() {
        return items;
    }

    public void setItems(List<ChooseCourseEntity> items) {
        this.items = items;
    }
}

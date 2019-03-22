package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 大学学历层次响应的根对象
 */
public class CollegeCategoryRootEntity {

    @InjectMap(name = "items")
    private List<CollegeCategory> items;

    public List<CollegeCategory> getItems() {
        return items;
    }

    public void setItems(List<CollegeCategory> items) {
        this.items = items;
    }
}

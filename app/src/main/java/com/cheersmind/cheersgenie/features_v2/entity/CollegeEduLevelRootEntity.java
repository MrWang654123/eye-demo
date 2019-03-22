package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 大学学历层次响应的根对象
 */
public class CollegeEduLevelRootEntity {

    @InjectMap(name = "items")
    private List<CollegeEduLevel> items;

    public List<CollegeEduLevel> getItems() {
        return items;
    }

    public void setItems(List<CollegeEduLevel> items) {
        this.items = items;
    }
}

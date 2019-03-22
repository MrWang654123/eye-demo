package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 大学省份响应的根对象
 */
public class CollegeProvinceRootEntity {

    @InjectMap(name = "items")
    private List<CollegeProvince> items;

    public List<CollegeProvince> getItems() {
        return items;
    }

    public void setItems(List<CollegeProvince> items) {
        this.items = items;
    }
}

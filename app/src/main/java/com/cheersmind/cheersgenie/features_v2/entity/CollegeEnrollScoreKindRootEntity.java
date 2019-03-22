package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 大学学历层次响应的根对象
 */
public class CollegeEnrollScoreKindRootEntity {

    @InjectMap(name = "items")
    private List<CollegeEnrollScoreKind> items;

    public List<CollegeEnrollScoreKind> getItems() {
        return items;
    }

    public void setItems(List<CollegeEnrollScoreKind> items) {
        this.items = items;
    }
}

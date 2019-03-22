package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 院校录取分数响应的根对象
 */
public class CollegeEnrollScoreRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<CollegeEnrollScoreItemEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CollegeEnrollScoreItemEntity> getItems() {
        return items;
    }

    public void setItems(List<CollegeEnrollScoreItemEntity> items) {
        this.items = items;
    }
}

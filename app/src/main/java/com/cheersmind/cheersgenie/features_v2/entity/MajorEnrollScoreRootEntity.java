package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 专业录取分数响应的根对象
 */
public class MajorEnrollScoreRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<MajorEnrollScoreItemEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<MajorEnrollScoreItemEntity> getItems() {
        return items;
    }

    public void setItems(List<MajorEnrollScoreItemEntity> items) {
        this.items = items;
    }
}

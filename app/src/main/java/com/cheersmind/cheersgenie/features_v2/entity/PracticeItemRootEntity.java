package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 实践子项列表响应的根对象
 */
public class PracticeItemRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<PracticeItemEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<PracticeItemEntity> getItems() {
        return items;
    }

    public void setItems(List<PracticeItemEntity> items) {
        this.items = items;
    }
}

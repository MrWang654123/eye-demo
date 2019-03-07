package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 我的实践列表响应的根对象
 */
public class PracticeRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<PracticeEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<PracticeEntity> getItems() {
        return items;
    }

    public void setItems(List<PracticeEntity> items) {
        this.items = items;
    }
}

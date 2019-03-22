package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 职业列表响应的根对象
 */
public class OccupationItemRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<OccupationItem> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<OccupationItem> getItems() {
        return items;
    }

    public void setItems(List<OccupationItem> items) {
        this.items = items;
    }
}

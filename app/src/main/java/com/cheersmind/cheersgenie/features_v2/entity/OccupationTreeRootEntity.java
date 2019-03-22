package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 行业树响应的根对象
 */
public class OccupationTreeRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<OccupationRealm> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<OccupationRealm> getItems() {
        return items;
    }

    public void setItems(List<OccupationRealm> items) {
        this.items = items;
    }
}

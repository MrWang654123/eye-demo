package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 发展档案列表响应的根对象
 */
public class TrackRecordRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<TrackRecordEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<TrackRecordEntity> getItems() {
        return items;
    }

    public void setItems(List<TrackRecordEntity> items) {
        this.items = items;
    }
}

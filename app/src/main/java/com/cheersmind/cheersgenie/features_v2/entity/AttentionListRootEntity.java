package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 关注列表响应的根对象
 */
public class AttentionListRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<AttentionEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<AttentionEntity> getItems() {
        return items;
    }

    public void setItems(List<AttentionEntity> items) {
        this.items = items;
    }
}

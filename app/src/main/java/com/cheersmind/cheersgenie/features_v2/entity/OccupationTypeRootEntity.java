package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 职业分类响应的根对象
 */
public class OccupationTypeRootEntity {

    @InjectMap(name = "items")
    private List<OccupationRealm> items;

    public List<OccupationRealm> getItems() {
        return items;
    }

    public void setItems(List<OccupationRealm> items) {
        this.items = items;
    }
}

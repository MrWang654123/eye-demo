package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 推荐专业响应的根对象
 */
public class RecommendMajorRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<RecommendMajor> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<RecommendMajor> getItems() {
        return items;
    }

    public void setItems(List<RecommendMajor> items) {
        this.items = items;
    }
}

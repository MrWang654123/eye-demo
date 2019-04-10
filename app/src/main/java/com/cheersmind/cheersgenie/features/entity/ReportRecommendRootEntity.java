package com.cheersmind.cheersgenie.features.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 报告推荐的根对象
 */
public class ReportRecommendRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<ReportRecommend> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ReportRecommend> getItems() {
        return items;
    }

    public void setItems(List<ReportRecommend> items) {
        this.items = items;
    }
}

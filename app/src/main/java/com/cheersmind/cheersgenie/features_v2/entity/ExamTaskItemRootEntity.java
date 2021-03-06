package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 测评任务列表响应的根对象
 */
public class ExamTaskItemRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<ExamTaskItemEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ExamTaskItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ExamTaskItemEntity> items) {
        this.items = items;
    }
}

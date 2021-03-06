package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 测评模块列表响应的根对象
 */
public class ExamModuleRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<ExamModuleEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ExamModuleEntity> getItems() {
        return items;
    }

    public void setItems(List<ExamModuleEntity> items) {
        this.items = items;
    }
}

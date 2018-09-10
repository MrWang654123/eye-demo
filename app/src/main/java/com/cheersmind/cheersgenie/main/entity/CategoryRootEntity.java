package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 分类列表响应的根对象
 */
public class CategoryRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<CategoryEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CategoryEntity> getItems() {
        return items;
    }

    public void setItems(List<CategoryEntity> items) {
        this.items = items;
    }
}

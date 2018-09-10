package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 积分列表响应的根对象
 */
public class IntegralRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<IntegralEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<IntegralEntity> getItems() {
        return items;
    }

    public void setItems(List<IntegralEntity> items) {
        this.items = items;
    }
}

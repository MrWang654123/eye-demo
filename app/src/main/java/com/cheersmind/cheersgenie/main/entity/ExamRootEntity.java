package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;


/**
 * 测评根对象
 */
public class ExamRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<ExamEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ExamEntity> getItems() {
        return items;
    }

    public void setItems(List<ExamEntity> items) {
        this.items = items;
    }

}

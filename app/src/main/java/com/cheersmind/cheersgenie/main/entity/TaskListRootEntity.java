package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 任务列表响应的根对象
 */
public class TaskListRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<TaskListEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<TaskListEntity> getItems() {
        return items;
    }

    public void setItems(List<TaskListEntity> items) {
        this.items = items;
    }
}

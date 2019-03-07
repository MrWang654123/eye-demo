package com.cheersmind.cheersgenie.features.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 评论列表响应的根对象
 */
public class CommentRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<CommentEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CommentEntity> getItems() {
        return items;
    }

    public void setItems(List<CommentEntity> items) {
        this.items = items;
    }
}

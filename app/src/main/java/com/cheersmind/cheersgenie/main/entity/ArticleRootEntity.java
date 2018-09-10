package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 文章列表响应的根对象
 */
public class ArticleRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<SimpleArticleEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SimpleArticleEntity> getItems() {
        return items;
    }

    public void setItems(List<SimpleArticleEntity> items) {
        this.items = items;
    }
}

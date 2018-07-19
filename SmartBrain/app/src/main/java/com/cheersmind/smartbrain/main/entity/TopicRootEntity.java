package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */

public class TopicRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<TopicInfoEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<TopicInfoEntity> getItems() {
        return items;
    }

    public void setItems(List<TopicInfoEntity> items) {
        this.items = items;
    }
}

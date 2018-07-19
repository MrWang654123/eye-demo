package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */

public class QuestionRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<QuestionInfoEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<QuestionInfoEntity> getItems() {
        return items;
    }

    public void setItems(List<QuestionInfoEntity> items) {
        this.items = items;
    }
}

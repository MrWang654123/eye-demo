package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.util.List;

/**
 * Created by Administrator on 2017/11/11.
 */

public class FlowerRecordRootEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<FlowerRecordInfoEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<FlowerRecordInfoEntity> getItems() {
        return items;
    }

    public void setItems(List<FlowerRecordInfoEntity> items) {
        this.items = items;
    }
}

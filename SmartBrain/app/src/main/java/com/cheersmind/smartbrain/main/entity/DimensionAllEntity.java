package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.util.List;

/**
 * Created by Administrator on 2017/11/15.
 */

public class DimensionAllEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<DimensionInfoEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DimensionInfoEntity> getItems() {
        return items;
    }

    public void setItems(List<DimensionInfoEntity> items) {
        this.items = items;
    }
}

package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.util.List;

/**
 * Created by Administrator on 2017/10/28.
 */

public class DimensionRootCEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<DimensionInfoChildEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DimensionInfoChildEntity> getItems() {
        return items;
    }

    public void setItems(List<DimensionInfoChildEntity> items) {
        this.items = items;
    }
}

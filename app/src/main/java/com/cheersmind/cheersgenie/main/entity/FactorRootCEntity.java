package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * Created by Administrator on 2017/10/28.
 */

public class FactorRootCEntity {

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private List<FactorInfoChildEntity> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<FactorInfoChildEntity> getItems() {
        return items;
    }

    public void setItems(List<FactorInfoChildEntity> items) {
        this.items = items;
    }

}

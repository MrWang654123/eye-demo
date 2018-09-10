package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 总积分
 */
public class TotalIntegralEntity {

    //总积分
    @InjectMap(name = "total")
    private int total;

    //可用积分
    @InjectMap(name = "consumable")
    private int consumable;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getConsumable() {
        return consumable;
    }

    public void setConsumable(int consumable) {
        this.consumable = consumable;
    }
}

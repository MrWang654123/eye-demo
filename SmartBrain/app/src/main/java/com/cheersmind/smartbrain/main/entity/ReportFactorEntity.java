package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/27.
 */

public class ReportFactorEntity implements Serializable {

    @InjectMap(name = "child_score")
    private double childScore;

    @InjectMap(name = "item_name")
    private String itemName;

    @InjectMap(name = "item_id")
    private String itemId;

    @InjectMap(name = "compare_score")
    private float compareScore;

    public double getChildScore() {
        return childScore;
    }

    public void setChildScore(double childScore) {
        this.childScore = childScore;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public float getCompareScore() {
        return compareScore;
    }

    public void setCompareScore(float compareScore) {
        this.compareScore = compareScore;
    }
}

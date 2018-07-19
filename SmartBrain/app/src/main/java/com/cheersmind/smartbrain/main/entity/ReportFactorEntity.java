package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/27.
 */

public class ReportFactorEntity implements Serializable {

    @InjectMap(name = "child_score")
    private String childScore;

    @InjectMap(name = "item_name")
    private String itemName;

    @InjectMap(name = "item_id")
    private String itemId;

    @InjectMap(name = "compare_score")
    private int compareScore;

    public String getChildScore() {
        return childScore;
    }

    public void setChildScore(String childScore) {
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

    public int getCompareScore() {
        return compareScore;
    }

    public void setCompareScore(int compareScore) {
        this.compareScore = compareScore;
    }
}

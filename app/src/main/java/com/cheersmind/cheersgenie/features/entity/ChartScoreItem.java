package com.cheersmind.cheersgenie.features.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 图表分数项
 */
public class ChartScoreItem implements Serializable {

    @InjectMap(name = "item_id")
    private String itemId;

    @InjectMap(name = "item_name")
    private String itemName;

    @InjectMap(name = "score")
    private double score;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}

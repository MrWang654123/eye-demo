package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 图表比较项
 */
public class ChartCompareItem implements Serializable {

    @InjectMap(name = "compare_id")
    private String compareId;

    @InjectMap(name = "compare_name")
    private String compareName;

    //分数项集合
    @InjectMap(name = "chart_datas")
    private List<ChartScoreItem> scoreItems;

    public String getCompareId() {
        return compareId;
    }

    public void setCompareId(String compareId) {
        this.compareId = compareId;
    }

    public String getCompareName() {
        return compareName;
    }

    public void setCompareName(String compareName) {
        this.compareName = compareName;
    }

    public List<ChartScoreItem> getScoreItems() {
        return scoreItems;
    }

    public void setScoreItems(List<ChartScoreItem> scoreItems) {
        this.scoreItems = scoreItems;
    }
}

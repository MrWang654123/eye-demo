package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 报告推荐的ACT分类
 */
public class ReportRecommendActType implements MultiItemEntity, Serializable {

    public ReportRecommendActType(List<OccupationCategory> items) {
        this.items = items;
    }

    private List<OccupationCategory> items;

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public ReportRecommendActType setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }

    public List<OccupationCategory> getItems() {
        return items;
    }

    public void setItems(List<OccupationCategory> items) {
        this.items = items;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 报告推荐的ACT分类
 */
public class ReportRecommendActType implements MultiItemEntity, Serializable {

    public ReportRecommendActType(List<ActType> items) {
        this.items = items;
    }

    private List<ActType> items;

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public ReportRecommendActType setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }

    public List<ActType> getItems() {
        return items;
    }

    public void setItems(List<ActType> items) {
        this.items = items;
    }
}

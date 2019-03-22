package com.cheersmind.cheersgenie.features.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 图表说明
 */
public class ChartItemDesc implements MultiItemEntity {

    public ChartItemDesc(String desc) {
        this.desc = desc;
    }

    //图表说明
    private String desc;

    //伸缩
    private boolean expand;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public ChartItemDesc setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }
}


package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * 报告子标题
 */
public class ReportSubTitleEntity implements MultiItemEntity, Serializable {

    private String title;

    public ReportSubTitleEntity(String title) {
        this.title = title;
    }

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public ReportSubTitleEntity setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

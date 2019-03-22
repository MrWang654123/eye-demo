package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 报告Mbti数据
 */
public class ReportMbtiData implements MultiItemEntity, Serializable {

    public ReportMbtiData(List<ExamMbtiData> items) {
        this.items = items;
    }

    private List<ExamMbtiData> items;

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public ReportMbtiData setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }

    public List<ExamMbtiData> getItems() {
        return items;
    }

    public void setItems(List<ExamMbtiData> items) {
        this.items = items;
    }
}

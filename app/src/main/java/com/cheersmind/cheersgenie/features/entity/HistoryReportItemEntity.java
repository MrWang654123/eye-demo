package com.cheersmind.cheersgenie.features.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 历史报告项
 */
public class HistoryReportItemEntity implements MultiItemEntity {
    //Table head项
    public static final int HEAD = 1;
    //Table body项
    public static final int ITEM = 2;

    //默认是body项
    private int itemType;

    public HistoryReportItemEntity() {
        this.itemType = ITEM;
    }

    public HistoryReportItemEntity(int itemType) {
        this.itemType = ITEM;
        this.itemType = itemType;
    }

    //结果
    @InjectMap(name = "result")
    private String result;

    //批次号
    @InjectMap(name = "batch")
    private String batch;

    //孩子测评ID
    @InjectMap(name = "child_exam_id")
    private String childExamId;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getChildExamId() {
        return childExamId;
    }

    public void setChildExamId(String childExamId) {
        this.childExamId = childExamId;
    }

    @Override
    public int getItemType() {
        return this.itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}

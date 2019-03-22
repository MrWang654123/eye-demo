package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 测评报告的响应根结果
 */
public class ExamMbtiData implements Serializable, MultiItemEntity {

    @InjectMap(name = "left")
    private double left;

    @InjectMap(name = "right")
    private double right;

    //结果
    @InjectMap(name = "result")
    private String result;

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public ExamMbtiData setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }
}

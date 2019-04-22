package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 报告评价
 */
public class ReportEvaluate implements MultiItemEntity {

    @InjectMap(name = "id")
    private String id;

    //评价标题
    @InjectMap(name = "title")
    private String title;

    @InjectMap(name = "type")
    private int type;

    //评价选项
    @InjectMap(name = "items")
    private List<ReportEvaluateItem> items;

    @InjectMap(name = "is_evaluate")
    private boolean evaluate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<ReportEvaluateItem> getItems() {
        return items;
    }

    public void setItems(List<ReportEvaluateItem> items) {
        this.items = items;
    }

    public boolean isEvaluate() {
        return evaluate;
    }

    public void setEvaluate(boolean evaluate) {
        this.evaluate = evaluate;
    }

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

}

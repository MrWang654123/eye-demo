package com.cheersmind.cheersgenie.features.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 报告推荐
 */
public class ReportRecommend implements MultiItemEntity {

    @InjectMap(name = "node_name")
    private String node_name;

    //标题
    @InjectMap(name = "elements")
    private List<ReportRecommendItem> items;


    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public List<ReportRecommendItem> getItems() {
        return items;
    }

    public void setItems(List<ReportRecommendItem> items) {
        this.items = items;
    }
}

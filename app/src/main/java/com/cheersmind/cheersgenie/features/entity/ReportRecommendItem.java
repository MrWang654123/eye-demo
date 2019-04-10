package com.cheersmind.cheersgenie.features.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 报告推荐子项
 */
public class ReportRecommendItem implements MultiItemEntity {

    @InjectMap(name = "element_id")
    private String element_id;

    @InjectMap(name = "element_name")
    private String element_name;

    @InjectMap(name = "element_type")
    private String element_type;


    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getElement_id() {
        return element_id;
    }

    public void setElement_id(String element_id) {
        this.element_id = element_id;
    }

    public String getElement_name() {
        return element_name;
    }

    public void setElement_name(String element_name) {
        this.element_name = element_name;
    }

    public String getElement_type() {
        return element_type;
    }

    public void setElement_type(String element_type) {
        this.element_type = element_type;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 霍兰德职业兴趣模型的分类（26个分类，6大群）
 */
public class ActType implements Serializable, MultiItemEntity {

    @InjectMap(name = "area_id")
    private String area_id;

    @InjectMap(name = "area_name")
    private String area_name;

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public ActType setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }
}

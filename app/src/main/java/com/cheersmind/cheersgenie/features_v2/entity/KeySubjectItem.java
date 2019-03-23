package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 重点学科项
 */
public class KeySubjectItem implements MultiItemEntity {

    public KeySubjectItem(String name) {
        this.name = name;
    }

    @InjectMap(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //布局类型
    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public KeySubjectItem setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }
}

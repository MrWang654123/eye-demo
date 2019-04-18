package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 发展档案子项横向Recycler
 */
public class DevelopmentRecordRecycler implements MultiItemEntity {

    //子项集合
    private List<DevelopmentRecordItem> items;

    public DevelopmentRecordRecycler(List<DevelopmentRecordItem> items) {
        this.items = items;
    }

    public List<DevelopmentRecordItem> getItems() {
        return items;
    }

    public void setItems(List<DevelopmentRecordItem> items) {
        this.items = items;
    }

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public DevelopmentRecordRecycler setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }
}

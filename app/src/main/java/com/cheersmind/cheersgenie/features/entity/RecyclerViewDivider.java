package com.cheersmind.cheersgenie.features.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 分割线
 */
public class RecyclerViewDivider implements MultiItemEntity {

    private int itemType;

    public RecyclerViewDivider(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}

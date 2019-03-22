package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features_v2.adapter.OccupationTreeRecyclerAdapter;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 行业
 */
public class OccupationItem implements MultiItemEntity, Serializable {

    //行业名称
    @InjectMap(name = "occupation_name")
    private String occupation_name;

    //行业ID
    @InjectMap(name = "occupation_id")
    private long occupation_id;

    //布局类型
    private int itemType = OccupationTreeRecyclerAdapter.LAYOUT_TYPE_LEVEL2;

    //最里层的子项是否是兄弟中的最后一个
    private boolean isLastInMaxLevel;

    public String getOccupation_name() {
        return occupation_name;
    }

    public void setOccupation_name(String occupation_name) {
        this.occupation_name = occupation_name;
    }

    public long getOccupation_id() {
        return occupation_id;
    }

    public void setOccupation_id(long occupation_id) {
        this.occupation_id = occupation_id;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isLastInMaxLevel() {
        return isLastInMaxLevel;
    }

    public void setLastInMaxLevel(boolean lastInMaxLevel) {
        isLastInMaxLevel = lastInMaxLevel;
    }
}

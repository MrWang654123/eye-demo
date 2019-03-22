package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features_v2.adapter.OccupationTreeRecyclerAdapter;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 行业所属门类
 */
public class OccupationCategory extends AbstractExpandableItem<OccupationItem> implements MultiItemEntity, Serializable {

    public OccupationCategory() {
        mExpandable = false;
    }

    //专业所属门类名称
    @InjectMap(name = "category")
    private String category;

    //行业集合
    @InjectMap(name = "occupations")
    private List<OccupationItem> occupationItems;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<OccupationItem> getOccupationItems() {
        return occupationItems;
    }

    public void setOccupationItems(List<OccupationItem> occupationItems) {
        this.occupationItems = occupationItems;
    }

    private int level = 1;
    private int itemType = OccupationTreeRecyclerAdapter.LAYOUT_TYPE_LEVEL1;
    //最里层的子项是否是兄弟中的最后一个
    private boolean isLastInMaxLevel;

    @Override
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

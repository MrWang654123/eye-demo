package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features_v2.adapter.MajorTreeRecyclerAdapter;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 专业所属门类
 */
public class MajorCategory extends AbstractExpandableItem<MajorItem> implements MultiItemEntity, Serializable {

    public MajorCategory() {
        mExpandable = false;
    }

    //专业所属门类名称
    @InjectMap(name = "category")
    private String category;

    //专业集合
    @InjectMap(name = "majors")
    private List<MajorItem> majorItems;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<MajorItem> getMajorItems() {
        return majorItems;
    }

    public void setMajorItems(List<MajorItem> majorItems) {
        this.majorItems = majorItems;
    }

    private int level = 1;
    private int itemType = MajorTreeRecyclerAdapter.LAYOUT_TYPE_LEVEL1;
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

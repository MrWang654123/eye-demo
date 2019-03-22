package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features_v2.adapter.MajorTreeRecyclerAdapter;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 专业所属学科
 */
public class MajorSubject extends AbstractExpandableItem<MajorCategory> implements MultiItemEntity, Serializable {

    public MajorSubject() {
        mExpandable = false;
    }

    @InjectMap(name = "subject")
    private String subject;

    //门类
    @InjectMap(name = "categorys")
    private List<MajorCategory> categorys;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<MajorCategory> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<MajorCategory> categorys) {
        this.categorys = categorys;
    }

    private int level = 0;
    private int itemType = MajorTreeRecyclerAdapter.LAYOUT_TYPE_LEVEL0;
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

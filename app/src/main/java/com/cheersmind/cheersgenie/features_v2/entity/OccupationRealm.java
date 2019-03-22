package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features_v2.adapter.OccupationTreeRecyclerAdapter;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 行业领域
 */
public class OccupationRealm extends AbstractExpandableItem<OccupationCategory> implements MultiItemEntity, Serializable {

    public OccupationRealm() {
        mExpandable = false;
    }

    @InjectMap(name = "realm")
    private String realm;

    //门类
    @InjectMap(name = "categorys")
    private List<OccupationCategory> categorys;

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public List<OccupationCategory> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<OccupationCategory> categorys) {
        this.categorys = categorys;
    }

    private int level = 0;
    private int itemType = OccupationTreeRecyclerAdapter.LAYOUT_TYPE_LEVEL0;
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

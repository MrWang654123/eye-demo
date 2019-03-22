package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 招生章程
 */
public class CollegeEnrollConstitution extends AbstractExpandableItem<CollegeEnrollConstitutionItem> implements MultiItemEntity, Serializable {

    //标题
    @InjectMap(name = "name")
    private String title;

    //招生办地址
    @InjectMap(name = "items")
    private List<CollegeEnrollConstitutionItem> items;

    //是否是兄弟中的第一个
    boolean isFirst;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CollegeEnrollConstitutionItem> getItems() {
        return items;
    }

    public void setItems(List<CollegeEnrollConstitutionItem> items) {
        this.items = items;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL0;
    }

}

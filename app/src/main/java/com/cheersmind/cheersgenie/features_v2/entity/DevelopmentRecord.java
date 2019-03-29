package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features.entity.ArticleCategory;
import com.cheersmind.cheersgenie.features.entity.ArticleTag;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 发展档案
 */
public class DevelopmentRecord extends AbstractExpandableItem<DevelopmentRecordItem> implements Serializable, MultiItemEntity {

    //名称
    @InjectMap(name = "capability_name")
    private String capability_name;

    //完成度
    @InjectMap(name = "finish_radio")
    private Double finish_radio;

    //子项集合
    @InjectMap(name = "dimensions")
    private List<DevelopmentRecordItem> items;

    public String getCapability_name() {
        return capability_name;
    }

    public void setCapability_name(String capability_name) {
        this.capability_name = capability_name;
    }

    public Double getFinish_radio() {
        return finish_radio;
    }

    public void setFinish_radio(Double finish_radio) {
        this.finish_radio = finish_radio;
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

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}

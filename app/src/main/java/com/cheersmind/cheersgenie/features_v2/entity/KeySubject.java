package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.json.JSONArray;

import java.io.Serializable;

/**
 * 重点学科
 */
public class KeySubject implements Serializable, MultiItemEntity {

    @InjectMap(name = "type")
    private String type;

    @InjectMap(name = "name")
    private String name;

    @InjectMap(name = "total")
    private int total;

    @InjectMap(name = "items")
    private String items;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    //布局类型
    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}

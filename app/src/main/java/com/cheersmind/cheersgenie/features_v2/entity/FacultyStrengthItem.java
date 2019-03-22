package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 师资力量项
 */
public class FacultyStrengthItem implements Serializable {

    //类型（例如："师资力量数据"）
    @InjectMap(name = "type")
    private String type;

    //数据集合
    @InjectMap(name = "items")
    private List<FacultyStrengthItemChild> items;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FacultyStrengthItemChild> getItems() {
        return items;
    }

    public void setItems(List<FacultyStrengthItemChild> items) {
        this.items = items;
    }
}

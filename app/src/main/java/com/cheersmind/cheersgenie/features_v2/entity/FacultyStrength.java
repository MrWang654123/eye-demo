package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 师资力量
 */
public class FacultyStrength implements Serializable {

    //概要
    @InjectMap(name = "summary")
    private String summary;

    //数据集合
    @InjectMap(name = "items")
    private List<FacultyStrengthItem> items;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<FacultyStrengthItem> getItems() {
        return items;
    }

    public void setItems(List<FacultyStrengthItem> items) {
        this.items = items;
    }
}

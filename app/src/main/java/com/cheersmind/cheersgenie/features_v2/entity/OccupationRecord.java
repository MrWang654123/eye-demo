package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 职业档案
 */
public class OccupationRecord extends BaseRecordItem {

    //推荐职业领域集群
    @InjectMap(name = "career_areas")
    private List<String> career_areas;

    //子项结果集
    @InjectMap(name = "items")
    private List<OccupationRecordItem> items;

    public List<String> getCareer_areas() {
        return career_areas;
    }

    public void setCareer_areas(List<String> career_areas) {
        this.career_areas = career_areas;
    }

    public List<OccupationRecordItem> getItems() {
        return items;
    }

    public void setItems(List<OccupationRecordItem> items) {
        this.items = items;
    }
}

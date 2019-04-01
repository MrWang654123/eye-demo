package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 职业档案
 */
public class OccupationRecord extends BaseRecordItem {

    //推荐职业领域集群
    @InjectMap(name = "act_areas")
    private List<OccupationCategory> act_areas;

    //子项结果集
    @InjectMap(name = "items")
    private List<SelectCourseRecordItem> items;

    public List<OccupationCategory> getAct_areas() {
        return act_areas;
    }

    public void setAct_areas(List<OccupationCategory> act_areas) {
        this.act_areas = act_areas;
    }

    public List<SelectCourseRecordItem> getItems() {
        return items;
    }

    public void setItems(List<SelectCourseRecordItem> items) {
        this.items = items;
    }
}

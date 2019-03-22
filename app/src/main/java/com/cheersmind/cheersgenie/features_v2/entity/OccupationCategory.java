package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 职业所属门类
 */
public class OccupationCategory extends DataSupport implements Serializable {

    //职业所属门类
    @InjectMap(name = "category")
    private String category;

    //ACT - 26种职业区域名称
    @InjectMap(name = "area_name")
    private String area_name;

    //ACT - 职业区域ID
    @InjectMap(name = "area_id")
    private long area_id;

    //职业类型
    private int type;

    private OccupationRealm occupationRealm;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public long getArea_id() {
        return area_id;
    }

    public void setArea_id(long area_id) {
        this.area_id = area_id;
    }

    public OccupationRealm getOccupationRealm() {
        return occupationRealm;
    }

    public void setOccupationRealm(OccupationRealm occupationRealm) {
        this.occupationRealm = occupationRealm;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    //统一的获取名称
    public String getName() {
        return area_id > 0 ? area_name : category;
    }

}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 勋章
 */
public class MedalEntity implements Serializable {

    //ID
    @InjectMap(name = "medal_id")
    private String medal_id;

    //名称
    @InjectMap(name = "medal_name")
    private String medal_name;

    //图标
    @InjectMap(name = "icon")
    private String icon;

    //描述
    @InjectMap(name = "description")
    private String description;

    //排序
    @InjectMap(name = "sort")
    private int sort;

    //状态，0-未获取，1-已获取。
    @InjectMap(name = "status")
    private int status;

    public String getMedal_id() {
        return medal_id;
    }

    public void setMedal_id(String medal_id) {
        this.medal_id = medal_id;
    }

    public String getMedal_name() {
        return medal_name;
    }

    public void setMedal_name(String medal_name) {
        this.medal_name = medal_name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

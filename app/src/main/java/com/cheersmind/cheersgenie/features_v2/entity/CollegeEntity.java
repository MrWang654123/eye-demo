package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 院校
 */
public class CollegeEntity implements Serializable {

    @InjectMap(name = "id")
    private String id;

    //中文名称
    @InjectMap(name = "cn_name")
    private String cn_name;

    //logo图片
    @InjectMap(name = "logo_url")
    private String logo_url;

    //英文名称
    @InjectMap(name = "en_name")
    private int en_name;

    //基本信息
    @InjectMap(name = "basic_info")
    private CollegeBasicInfo basicInfo;

    //是否选中
    private boolean isSelected;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCn_name() {
        return cn_name;
    }

    public void setCn_name(String cn_name) {
        this.cn_name = cn_name;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public int getEn_name() {
        return en_name;
    }

    public void setEn_name(int en_name) {
        this.en_name = en_name;
    }

    public CollegeBasicInfo getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(CollegeBasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}

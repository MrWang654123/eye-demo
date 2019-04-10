package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 院校
 */
public class CollegeEntity implements Serializable {

    @InjectMap(name = "id")
    private String id;

    //中文名称
    @InjectMap(name = "cn_name")
    private String cn_name;

    //英文名称
    @InjectMap(name = "en_name")
    private int en_name;

    //logo图片
    @InjectMap(name = "logo_url")
    private String logo_url;

    @InjectMap(name = "background_img")
    private String background_img;

    //城市
    @InjectMap(name = "city_data")
    private String city_data;

    //省份
    @InjectMap(name = "state")
    private String state;

    //学历层次
    @InjectMap(name = "china_degree")
    private String china_degree;

    //基本信息
    @InjectMap(name = "basic_info")
    private CollegeBasicInfo basicInfo;

    //专业信息
    @InjectMap(name = "major_info")
    private MajorItem major;

    //专业信息
    @InjectMap(name = "ranking")
    private Map mapRank;

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

    public String getBackground_img() {
        return background_img;
    }

    public void setBackground_img(String background_img) {
        this.background_img = background_img;
    }

    public String getCity_data() {
        return city_data;
    }

    public void setCity_data(String city_data) {
        this.city_data = city_data;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getChina_degree() {
        return china_degree;
    }

    public void setChina_degree(String china_degree) {
        this.china_degree = china_degree;
    }

    public MajorItem getMajor() {
        return major;
    }

    public void setMajor(MajorItem major) {
        this.major = major;
    }

    public Map getMapRank() {
        return mapRank;
    }

    public void setMapRank(Map mapRank) {
        this.mapRank = mapRank;
    }
}

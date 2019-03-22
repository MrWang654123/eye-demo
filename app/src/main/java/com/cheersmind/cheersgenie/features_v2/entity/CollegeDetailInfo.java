package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 院校详情信息
 */
public class CollegeDetailInfo implements Serializable {

    //Logo
    @InjectMap(name = "id")
    private String id;

    //中文名
    @InjectMap(name = "cn_name")
    private String cn_name;

    //英文名
    @InjectMap(name = "en_name")
    private String en_name;

    //Logo
    @InjectMap(name = "logo_url")
    private String logo_url;

    //背景图
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

    //简介
    @InjectMap(name = "brief_introduction")
    private String brief_introduction;

    //基本信息
    @InjectMap(name = "basic_info")
    private CollegeBasicInfo basicInfo;

    //排名
    @InjectMap(name = "ranking")
    private List<CollegeRankResultItem> ranking;

    //师资力量
    @InjectMap(name = "faculty_strength")
    private FacultyStrength faculty_strength;

    //性别比例
    @InjectMap(name = "student_ratio")
    private CollegeGenderRadio genderRadio;

    //在校生数据（国际生人数、研究生人数）
    @InjectMap(name = "student_data")
    private CollegeStudentData studentData;

    public String getBrief_introduction() {
        return brief_introduction;
    }

    public void setBrief_introduction(String brief_introduction) {
        this.brief_introduction = brief_introduction;
    }

    public CollegeBasicInfo getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(CollegeBasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public List<CollegeRankResultItem> getRanking() {
        return ranking;
    }

    public void setRanking(List<CollegeRankResultItem> ranking) {
        this.ranking = ranking;
    }

    public FacultyStrength getFaculty_strength() {
        return faculty_strength;
    }

    public void setFaculty_strength(FacultyStrength faculty_strength) {
        this.faculty_strength = faculty_strength;
    }

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

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
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

    public CollegeGenderRadio getGenderRadio() {
        return genderRadio;
    }

    public void setGenderRadio(CollegeGenderRadio genderRadio) {
        this.genderRadio = genderRadio;
    }

    public CollegeStudentData getStudentData() {
        return studentData;
    }

    public void setStudentData(CollegeStudentData studentData) {
        this.studentData = studentData;
    }
}

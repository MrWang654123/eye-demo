package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 高考选科档案
 */
public class SelectCourseRecord extends BaseRecordItem {

    //最终选科
    @InjectMap(name = "custom_subjects")
    private List<String> custom_subjects;

    //最终选科代码
    @InjectMap(name = "custom_subject_codes")
    private List<String> custom_subject_codes;

    //系统推荐选科
    @InjectMap(name = "recommend_subjects")
    private List<String> recommend_subjects;

    //系统推荐选科代码
    @InjectMap(name = "recommend_subject_codes")
    private List<String> recommend_subject_codes;

    //最终选科可报专业比例
    @InjectMap(name = "custom_major_per")
    private double custom_major_per;

    //最终选科高要求专业比例
    @InjectMap(name = "custom_high_major_per")
    private double custom_high_major_per;

    //系统推荐选科可报专业比例
    @InjectMap(name = "recommend_major_per")
    private double recommend_major_per;

    //系统推荐选科高要求专业比例
    @InjectMap(name = "recommend_high_major_per")
    private double recommend_high_major_per;

    //子项结果集
    @InjectMap(name = "items")
    private List<SelectCourseRecordItem> items;

    public List<String> getCustom_subjects() {
        return custom_subjects;
    }

    public void setCustom_subjects(List<String> custom_subjects) {
        this.custom_subjects = custom_subjects;
    }

    public List<String> getCustom_subject_codes() {
        return custom_subject_codes;
    }

    public void setCustom_subject_codes(List<String> custom_subject_codes) {
        this.custom_subject_codes = custom_subject_codes;
    }

    public List<String> getRecommend_subjects() {
        return recommend_subjects;
    }

    public void setRecommend_subjects(List<String> recommend_subjects) {
        this.recommend_subjects = recommend_subjects;
    }

    public List<String> getRecommend_subject_codes() {
        return recommend_subject_codes;
    }

    public void setRecommend_subject_codes(List<String> recommend_subject_codes) {
        this.recommend_subject_codes = recommend_subject_codes;
    }

    public double getCustom_major_per() {
        return custom_major_per;
    }

    public void setCustom_major_per(double custom_major_per) {
        this.custom_major_per = custom_major_per;
    }

    public double getCustom_high_major_per() {
        return custom_high_major_per;
    }

    public void setCustom_high_major_per(double custom_high_major_per) {
        this.custom_high_major_per = custom_high_major_per;
    }

    public double getRecommend_major_per() {
        return recommend_major_per;
    }

    public void setRecommend_major_per(double recommend_major_per) {
        this.recommend_major_per = recommend_major_per;
    }

    public double getRecommend_high_major_per() {
        return recommend_high_major_per;
    }

    public void setRecommend_high_major_per(double recommend_high_major_per) {
        this.recommend_high_major_per = recommend_high_major_per;
    }

    public List<SelectCourseRecordItem> getItems() {
        return items;
    }

    public void setItems(List<SelectCourseRecordItem> items) {
        this.items = items;
    }
}

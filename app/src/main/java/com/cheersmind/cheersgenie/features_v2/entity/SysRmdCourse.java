package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 系统推荐选科
 */
public class SysRmdCourse extends BaseRecordItem {

    //系统推荐选科
    @InjectMap(name = "recommend_subjects")
    private List<String> recommend_subjects;

    //系统推荐选科代码
    @InjectMap(name = "recommend_subject_codes")
    private List<String> recommend_subject_codes;

    //系统推荐选科可报专业比例
    @InjectMap(name = "recommend_major_per")
    private Double recommend_major_per;

    //系统推荐选科高要求专业比例
    @InjectMap(name = "recommend_high_major_per")
    private Double recommend_high_major_per;

    //子项结果集
    @InjectMap(name = "items")
    private List<SysRmdCourseItem> items;

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

    public Double getRecommend_major_per() {
        return recommend_major_per;
    }

    public void setRecommend_major_per(Double recommend_major_per) {
        this.recommend_major_per = recommend_major_per;
    }

    public Double getRecommend_high_major_per() {
        return recommend_high_major_per;
    }

    public void setRecommend_high_major_per(Double recommend_high_major_per) {
        this.recommend_high_major_per = recommend_high_major_per;
    }

    public List<SysRmdCourseItem> getItems() {
        return items;
    }

    public void setItems(List<SysRmdCourseItem> items) {
        this.items = items;
    }
}

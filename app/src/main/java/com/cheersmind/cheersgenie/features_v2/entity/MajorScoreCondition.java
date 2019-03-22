package com.cheersmind.cheersgenie.features_v2.entity;

/**
 * 专业录取分数的筛选条件
 */
public class MajorScoreCondition {

    //生源地省份
    private CollegeProvince province;

    //年份
    private String year;

    //文理科
    private CollegeEnrollScoreKind kind;

    public CollegeProvince getProvince() {
        return province;
    }

    public void setProvince(CollegeProvince province) {
        this.province = province;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public CollegeEnrollScoreKind getKind() {
        return kind;
    }

    public void setKind(CollegeEnrollScoreKind kind) {
        this.kind = kind;
    }
}

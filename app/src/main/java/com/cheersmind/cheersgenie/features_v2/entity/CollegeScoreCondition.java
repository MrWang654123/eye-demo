package com.cheersmind.cheersgenie.features_v2.entity;

/**
 * 学校录取分数的筛选条件
 */
public class CollegeScoreCondition {

    //生源地省份
    private CollegeProvince province;

    //文理科
    private CollegeEnrollScoreKind kind;

    public CollegeProvince getProvince() {
        return province;
    }

    public void setProvince(CollegeProvince province) {
        this.province = province;
    }

    public CollegeEnrollScoreKind getKind() {
        return kind;
    }

    public void setKind(CollegeEnrollScoreKind kind) {
        this.kind = kind;
    }
}

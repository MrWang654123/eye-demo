package com.cheersmind.cheersgenie.features_v2.dto;

/**
 * 学校录取分数
 */
public class CollegeEnrollScoreDto {

    public CollegeEnrollScoreDto() {
    }

    public CollegeEnrollScoreDto(String province, String kind, String collegeName) {
        this.province = province;
        this.kind = kind;
        this.collegeName = collegeName;
    }

    //生源地省份
    private String province;

    //文理科
    private String kind;

    //院校名称
    private String collegeName;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }
}

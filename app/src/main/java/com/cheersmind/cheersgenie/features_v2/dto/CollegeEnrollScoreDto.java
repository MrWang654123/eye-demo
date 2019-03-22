package com.cheersmind.cheersgenie.features_v2.dto;

/**
 * 学校录取分数
 */
public class CollegeEnrollScoreDto {

    public CollegeEnrollScoreDto() {
    }

    public CollegeEnrollScoreDto(String province, String year, String kind) {
        this.province = province;
        this.year = year;
        this.kind = kind;
    }

    //生源地省份
    private String province;

    //年份
    private String year;

    //文理科
    private String kind;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}

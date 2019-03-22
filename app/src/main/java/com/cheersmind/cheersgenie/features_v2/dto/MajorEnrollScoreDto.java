package com.cheersmind.cheersgenie.features_v2.dto;

/**
 * 专业录取分数
 */
public class MajorEnrollScoreDto {

    public MajorEnrollScoreDto() {
    }

    public MajorEnrollScoreDto(String province, String year, String kind) {
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

    //批次前缀
    private String batchPre;

    //批次后缀
    private String batchSuf;

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

    public String getBatchPre() {
        return batchPre;
    }

    public void setBatchPre(String batchPre) {
        this.batchPre = batchPre;
    }

    public String getBatchSuf() {
        return batchSuf;
    }

    public void setBatchSuf(String batchSuf) {
        this.batchSuf = batchSuf;
    }
}

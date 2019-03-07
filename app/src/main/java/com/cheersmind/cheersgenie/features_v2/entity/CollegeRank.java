package com.cheersmind.cheersgenie.features_v2.entity;

/**
 * 大学排名
 */
public class CollegeRank {

    //区域
    private CollegeRankArea collegeRankArea;

    //院校类别
    private CollegeRankCategory collegeRankCategory;

    //主题
    private CollegeRankSubject collegeRankSubject;

    public CollegeRankArea getCollegeRankArea() {
        return collegeRankArea;
    }

    public void setCollegeRankArea(CollegeRankArea collegeRankArea) {
        this.collegeRankArea = collegeRankArea;
    }

    public CollegeRankCategory getCollegeRankCategory() {
        return collegeRankCategory;
    }

    public void setCollegeRankCategory(CollegeRankCategory collegeRankCategory) {
        this.collegeRankCategory = collegeRankCategory;
    }

    public CollegeRankSubject getCollegeRankSubject() {
        return collegeRankSubject;
    }

    public void setCollegeRankSubject(CollegeRankSubject collegeRankSubject) {
        this.collegeRankSubject = collegeRankSubject;
    }
}

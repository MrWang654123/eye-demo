package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 专业详情
 */
public class MajorDetail implements Serializable {

    //专业名称
    @InjectMap(name = "major_name")
    private String major_name;

    //专业代码
    @InjectMap(name = "major_code")
    private String major_code;

    //学位
    @InjectMap(name = "degree")
    private String degree;

    //学制
    @InjectMap(name = "learn_year")
    private int learn_year;

    //所属学科
    @InjectMap(name = "subject")
    private String subject;

    //所属门类
    @InjectMap(name = "category")
    private String category;

    //开设课程
    @InjectMap(name = "course")
    private String course;

    //专业介绍
    @InjectMap(name = "introduces")
    private List<MajorIntroduce> introduces;

    //对口职业
    @InjectMap(name = "occupations")
    private List<OccupationItem> suitOccupations;

    //就业方向
    @InjectMap(name = "employment")
    private String employment;

    public String getMajor_name() {
        return major_name;
    }

    public void setMajor_name(String major_name) {
        this.major_name = major_name;
    }

    public String getMajor_code() {
        return major_code;
    }

    public void setMajor_code(String major_code) {
        this.major_code = major_code;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public int getLearn_year() {
        return learn_year;
    }

    public void setLearn_year(int learn_year) {
        this.learn_year = learn_year;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public List<MajorIntroduce> getIntroduces() {
        return introduces;
    }

    public void setIntroduces(List<MajorIntroduce> introduces) {
        this.introduces = introduces;
    }

    public List<OccupationItem> getSuitOccupations() {
        return suitOccupations;
    }

    public void setSuitOccupations(List<OccupationItem> suitOccupations) {
        this.suitOccupations = suitOccupations;
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }
}

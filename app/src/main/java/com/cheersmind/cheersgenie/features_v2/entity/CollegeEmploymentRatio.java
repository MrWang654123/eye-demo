package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 大学的总体就业率
 */
public class CollegeEmploymentRatio implements Serializable {

    //ID
    @InjectMap(name = "id")
    private long id;

    //学校ID
    @InjectMap(name = "university_id")
    private long university_id;

    //等级英文名称
    @InjectMap(name = "degree_name")
    private String degree_name;

    //等级中文名称
    @InjectMap(name = "degree")
    private String degree;

    //比率
    @InjectMap(name = "ratio")
    private double ratio;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUniversity_id() {
        return university_id;
    }

    public void setUniversity_id(long university_id) {
        this.university_id = university_id;
    }

    public String getDegree_name() {
        return degree_name;
    }

    public void setDegree_name(String degree_name) {
        this.degree_name = degree_name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}

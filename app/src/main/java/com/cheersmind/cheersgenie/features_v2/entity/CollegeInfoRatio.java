package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 大学的相关信息的比率
 */
public class CollegeInfoRatio implements Serializable {

    //ID
    @InjectMap(name = "id")
    private long id;

    //学校ID
    @InjectMap(name = "university_id")
    private long university_id;

    //名称
    @InjectMap(name = "degree")
    private String degree;

    //类型
    @InjectMap(name = "type")
    private String type;

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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}

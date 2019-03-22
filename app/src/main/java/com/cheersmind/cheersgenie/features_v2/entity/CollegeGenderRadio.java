package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 院校性别比例
 */
public class CollegeGenderRadio implements Serializable {

    @InjectMap(name = "university_id")
    private String university_id;

    //女生比例
    @InjectMap(name = "women_ratio")
    private double women_ratio;

    //男生比例
    @InjectMap(name = "men_ratio")
    private double men_ratio;

    //学生总数
    @InjectMap(name = "students_total")
    private int students_total;

    public String getUniversity_id() {
        return university_id;
    }

    public void setUniversity_id(String university_id) {
        this.university_id = university_id;
    }

    public double getWomen_ratio() {
        return women_ratio;
    }

    public void setWomen_ratio(double women_ratio) {
        this.women_ratio = women_ratio;
    }

    public double getMen_ratio() {
        return men_ratio;
    }

    public void setMen_ratio(double men_ratio) {
        this.men_ratio = men_ratio;
    }

    public int getStudents_total() {
        return students_total;
    }

    public void setStudents_total(int students_total) {
        this.students_total = students_total;
    }
}

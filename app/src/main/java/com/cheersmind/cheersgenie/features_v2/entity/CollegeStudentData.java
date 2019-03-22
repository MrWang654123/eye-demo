package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 在校生数据
 */
public class CollegeStudentData implements Serializable {

    @InjectMap(name = "university_id")
    private String university_id;

    //国际学生人数
    @InjectMap(name = "international_students")
    private int international_students;

    //本科生数量
    @InjectMap(name = "undergraduate_students")
    private int undergraduate_students;

    //研究生数量
    @InjectMap(name = "postgraduates_students")
    private int postgraduates_students;

    public String getUniversity_id() {
        return university_id;
    }

    public void setUniversity_id(String university_id) {
        this.university_id = university_id;
    }

    public int getInternational_students() {
        return international_students;
    }

    public void setInternational_students(int international_students) {
        this.international_students = international_students;
    }

    public int getUndergraduate_students() {
        return undergraduate_students;
    }

    public void setUndergraduate_students(int undergraduate_students) {
        this.undergraduate_students = undergraduate_students;
    }

    public int getPostgraduates_students() {
        return postgraduates_students;
    }

    public void setPostgraduates_students(int postgraduates_students) {
        this.postgraduates_students = postgraduates_students;
    }
}

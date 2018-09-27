package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 班级信息
 */
public class ClassInfoEntity extends DataSupport implements Serializable {

    //班级名称
    @InjectMap(name = "class_name")
    private String className;

    //年级名称
    @InjectMap(name = "grade_name")
    private String gradeName;

    //学校名称
    @InjectMap(name = "school_name")
    private String schoolName;

    //学段：1-幼儿园，2-小学，3-初中，4-高中
    @InjectMap(name = "period")
    private int period;

    @Override
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

}


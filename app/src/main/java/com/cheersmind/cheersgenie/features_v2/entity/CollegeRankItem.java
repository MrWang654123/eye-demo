package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 大学的排名项
 */
public class CollegeRankItem extends DataSupport implements Serializable {

    //编码
    @InjectMap(name = "code")
    private String code;

    //名称
    @InjectMap(name = "name")
    private String name;

    private CollegeEduLevel collegeEduLevel;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CollegeEduLevel getCollegeEduLevel() {
        return collegeEduLevel;
    }

    public void setCollegeEduLevel(CollegeEduLevel collegeEduLevel) {
        this.collegeEduLevel = collegeEduLevel;
    }
}

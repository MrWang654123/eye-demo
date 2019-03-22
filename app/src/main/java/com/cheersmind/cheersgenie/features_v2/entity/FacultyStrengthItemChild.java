package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 师资力量项
 */
public class FacultyStrengthItemChild implements Serializable {

    //院校ID
    @InjectMap(name = "university_id")
    private String university_id;

    //类型（例如："师资力量数据"）
    @InjectMap(name = "type")
    private String type;

    @InjectMap(name = "name")
    private String name;

    //总数
    @InjectMap(name = "total")
    private int total;

    //ID
    @InjectMap(name = "id")
    private long id;

    public String getUniversity_id() {
        return university_id;
    }

    public void setUniversity_id(String university_id) {
        this.university_id = university_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

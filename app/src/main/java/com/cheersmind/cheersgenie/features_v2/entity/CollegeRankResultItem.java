package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 大学的排名结果项
 */
public class CollegeRankResultItem extends DataSupport implements Serializable {

    //键
    @InjectMap(name = "code")
    private String code;

    //名称
    @InjectMap(name = "name")
    private String name;

    //排名值
    @InjectMap(name = "value")
    private int value;

    //类型
    @InjectMap(name = "type")
    private int type;

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

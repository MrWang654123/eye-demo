package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 职业类型
 */
public class OccupationType {

    public OccupationType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    @InjectMap(name = "type")
    private int type;

    @InjectMap(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

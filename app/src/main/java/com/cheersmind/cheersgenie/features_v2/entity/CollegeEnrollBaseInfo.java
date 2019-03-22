package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 大学招生基础信息
 */
public class CollegeEnrollBaseInfo implements Serializable {

    //招生办信息
    @InjectMap(name = "enrollment_office")
    private CollegeEnrollOffice enrollOffice;

    //招生章程
    @InjectMap(name = "enrollment_navigation")
    private List<CollegeEnrollConstitution> items;

    public CollegeEnrollOffice getEnrollOffice() {
        return enrollOffice;
    }

    public void setEnrollOffice(CollegeEnrollOffice enrollOffice) {
        this.enrollOffice = enrollOffice;
    }

    public List<CollegeEnrollConstitution> getItems() {
        return items;
    }

    public void setItems(List<CollegeEnrollConstitution> items) {
        this.items = items;
    }
}

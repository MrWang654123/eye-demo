package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;
import com.contrarywind.interfaces.IPickerViewData;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 大学的省份
 */
public class CollegeProvince extends DataSupport implements Serializable, IPickerViewData {

    //编码
    @InjectMap(name = "code")
    private String code;

    //名称
    @InjectMap(name = "name")
    private String name;

    public CollegeProvince() {
    }

    public CollegeProvince(String code, String name) {
        this.code = code;
        this.name = name;
    }

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

    @Override
    public String getPickerViewText() {
        return this.name;
    }
}

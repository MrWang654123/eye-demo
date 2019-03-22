package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;
import com.contrarywind.interfaces.IPickerViewData;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 院校录取文理分科
 */
public class CollegeEnrollScoreKind extends DataSupport implements Serializable, IPickerViewData {

    public CollegeEnrollScoreKind() {
    }

    public CollegeEnrollScoreKind(String kind) {
        this.kind = kind;
    }

    //文理分科或者不分
    @InjectMap(name = "kind")
    private String kind;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String getPickerViewText() {
        return this.kind;
    }
}

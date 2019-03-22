package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 招生章程具体项
 */
public class CollegeEnrollConstitutionItem implements MultiItemEntity, Serializable {

    //标题
    @InjectMap(name = "name")
    private String name;

    //url地址
    @InjectMap(name = "url")
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int getItemType() {
        return Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL1;
    }
}

package com.cheersmind.cheersgenie.features.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 分类
 */
public class CategoryEntity implements Serializable {

    @InjectMap(name = "id")
    private String id;

    //名称
    @InjectMap(name = "name")
    private String name;

    //背景图
    @InjectMap(name = "background_img")
    private String backgroundImg;

    //状态栏背景颜色
    @InjectMap(name = "background_color")
    private String backgroundColor;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackgroundImg() {
        return backgroundImg;
    }

    public void setBackgroundImg(String backgroundImg) {
        this.backgroundImg = backgroundImg;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}

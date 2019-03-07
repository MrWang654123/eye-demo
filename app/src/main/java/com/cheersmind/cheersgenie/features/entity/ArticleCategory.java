package com.cheersmind.cheersgenie.features.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 文章类型
 */
public class ArticleCategory extends DataSupport implements Serializable {

    //id
    @InjectMap(name = "id")
    private String id;

    //名称
    @InjectMap(name = "name")
    private String name;

    //背景图
    @InjectMap(name = "background_img")
    private String backgroundImg;

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
}

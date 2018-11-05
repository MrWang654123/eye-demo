package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;


/**
 * 因子报告结果
 */
public class FactorResultEntity implements Serializable {

    @InjectMap(name = "content")
    private String content;

    @InjectMap(name = "color")
    private String color;

    @InjectMap(name = "title")
    private String title;

    @InjectMap(name = "relation_id")
    private String relationId;

    @InjectMap(name = "relation_name")
    private String relationName;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

}

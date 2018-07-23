package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/27.
 */

public class ReportResultEntity implements Serializable {

    @InjectMap(name = "content")
    private String content;

    @InjectMap(name = "color")
    private String color;

    @InjectMap(name = "description")
    private String description;

    @InjectMap(name = "title")
    private String title;

    @InjectMap(name = "relation_id")
    private String relationId;

    @InjectMap(name = "result")
    private String result;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

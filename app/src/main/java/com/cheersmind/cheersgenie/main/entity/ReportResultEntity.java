package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/6/27.
 */

public class ReportResultEntity implements Serializable {

    //标题的前缀语句
    @InjectMap(name = "header")
    private String header;

    //标题（结果）
    @InjectMap(name = "title")
    private String title;

    //标题颜色
    @InjectMap(name = "color")
    private String color;

    //评价
    @InjectMap(name = "content")
    private String content;

    //图表说明
    @InjectMap(name = "description")
    private String description;

    @InjectMap(name = "relation_id")
    private String relationId;

    @InjectMap(name = "result")
    private String result;

    //因子结果
    @InjectMap(name = "factor_result")
    private List<FactorResultEntity> factorResultList;

    //因子结果拼接的文本（标题+内容）
    private String factorResultText;


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

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<FactorResultEntity> getFactorResultList() {
        return factorResultList;
    }

    public void setFactorResultList(List<FactorResultEntity> factorResultList) {
        this.factorResultList = factorResultList;
    }

    public String getFactorResultText() {
        return factorResultText;
    }

    public void setFactorResultText(String factorResultText) {
        this.factorResultText = factorResultText;
    }

}

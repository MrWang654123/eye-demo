package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 报告评价项
 */
public class ReportEvaluateItem {

    @InjectMap(name = "id")
    private Integer id;

    //评价项ID
    @InjectMap(name = "evaluate_title_id")
    private String evaluate_title_id;

    //评价内容
    @InjectMap(name = "content")
    private String content;

    @InjectMap(name = "value")
    private Double value;

    //是否评价 true 选中
    @InjectMap(name = "is_evaluate")
    private boolean evaluate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEvaluate_title_id() {
        return evaluate_title_id;
    }

    public void setEvaluate_title_id(String evaluate_title_id) {
        this.evaluate_title_id = evaluate_title_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public boolean isEvaluate() {
        return evaluate;
    }

    public void setEvaluate(boolean evaluate) {
        this.evaluate = evaluate;
    }

}

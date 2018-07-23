package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/29.
 */

public class OptionsEntity implements Serializable{

    //    "content": "完全不符合",
//            "score": 1,
//            "orderby": 1,
//            "option_id": "C11B53EF-AC34-B912-ED44-552D190D04D8",
//            "type": 1,
//            "question_id": "79CD64F8-1117-08C1-FA38-8C7422F3D27E"

    @InjectMap(name = "content")
    private String content;

    @InjectMap(name = "score")
    private int score;

    @InjectMap(name = "orderby")
    private int orderby;

    @InjectMap(name = "option_id")
    private String optionId;

    @InjectMap(name = "type")
    private int type;

    @InjectMap(name = "question_id")
    private String questionId;

    @InjectMap(name = "show_value")
    private int showValue;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getOrderby() {
        return orderby;
    }

    public void setOrderby(int orderby) {
        this.orderby = orderby;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public int getShowValue() {
        return showValue;
    }

    public void setShowValue(int showValue) {
        this.showValue = showValue;
    }
}

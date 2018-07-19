package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */

public class QuestionInfoEntity implements Serializable{

    @InjectMap(name = "question_id")
    private String questionId;

    @InjectMap(name = "factor_id")
    private String factorId;

    @InjectMap(name = "stem")
    private String stem;

    @InjectMap(name = "type")
    private int type;

    @InjectMap(name = "show_type")
    private int showType;

    @InjectMap(name = "orderby")
    private int orderby;

    @InjectMap(name = "options")
    private List<OptionsEntity> options;

    @InjectMap(name = "child_question")
    private QuestionInfoChildEntity childQuestion;

    private boolean hasAnswer = false;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getFactorId() {
        return factorId;
    }

    public void setFactorId(String factorId) {
        this.factorId = factorId;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrderby() {
        return orderby;
    }

    public void setOrderby(int orderby) {
        this.orderby = orderby;
    }

    public List<OptionsEntity> getOptions() {
        return options;
    }

    public void setOptions(List<OptionsEntity> options) {
        this.options = options;
    }

    public QuestionInfoChildEntity getChildQuestion() {
        return childQuestion;
    }

    public void setChildQuestion(QuestionInfoChildEntity childQuestion) {
        this.childQuestion = childQuestion;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public boolean isHasAnswer() {
        return hasAnswer;
    }

    public void setHasAnswer(boolean hasAnswer) {
        this.hasAnswer = hasAnswer;
    }
}

package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/4.
 */

public class QuestionInfoChildEntity implements Serializable{

//    "id": "988c2658-9976-423c-9bf1-9a4065591aa4",
//            "exam_id": "1",
//            "user_id": 2109064312,
//            "child_id": "1",
//            "child_factor_id": "286e4b5e-5365-4c63-8989-21e77ee0c0c3",
//            "question_id": "5db31450-08fa-0ba1-1beb-c937b0d81b58",
//            "option_id": "e3c4da06-e9e4-d595-10c4-c508983e730b",
//            "score": 3,
//            "option_text": "",
//            "create_time": "2017-12-12T20:49:00.601+0800",
//            "update_time": "2017-12-12T20:49:00.601+0800",
//            "flowers": 12


    @InjectMap(name = "id")
    private String childQuestionId;

    @InjectMap(name = "exam_id")
    private String examId;

    @InjectMap(name = "update_time")
    private String updateTime;

    @InjectMap(name = "child_id")
    private String childId;

    @InjectMap(name = "create_time")
    private String createTime;

    @InjectMap(name = "score")
    private String score;

    @InjectMap(name = "child_factor_id")
    private String childFactorId;

    @InjectMap(name = "user_id")
    private String userId;

    @InjectMap(name = "option_id")
    private String optionId;

    @InjectMap(name = "question_id")
    private String questionId;

    @InjectMap(name = "option_text")
    private String optionText;

    @InjectMap(name = "flowers")
    private int flowers;

    public String getChildQuestionId() {
        return childQuestionId;
    }

    public void setChildQuestionId(String childQuestionId) {
        this.childQuestionId = childQuestionId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getChildFactorId() {
        return childFactorId;
    }

    public void setChildFactorId(String childFactorId) {
        this.childFactorId = childFactorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public int getFlowers() {
        return flowers;
    }

    public void setFlowers(int flowers) {
        this.flowers = flowers;
    }
}

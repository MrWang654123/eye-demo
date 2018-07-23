package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/28.
 */

public class FactorInfoChildEntity implements Serializable{
//    "child_dimension_id": "acbdca7a-2f04-46c7-8af2-9cbbabfbc227",
//            "status": 1,
//            "cost_time": 50000,
//            "avg_score": 3,
//            "id": "24567793-c67a-4eb8-9dc3-6dfa8e13062e",
//            "exam_id": "1",
//            "update_time": "2017-11-04T16:07:34.000+0800",
//            "factor_id": "7D565534-76D6-2FB5-0CE7-FE8949A968E1",
//            "complete_count": 16,
//            "child_id": "1",
//            "create_time": "2017-10-28T18:02:11.000+0800",
//            "user_id": 2109096778,
//            "flowers": 111,
//            "question_count": null

    @InjectMap(name = "child_dimension_id")
    private String childDimensionId;

    @InjectMap(name = "status")
    private int status;//0进行中，1已提交，2已完成

    @InjectMap(name = "cost_time")
    private int costTime;

    @InjectMap(name = "avg_score")
    private int avgScore;

    @InjectMap(name = "id")
    private String childFactorId;

    @InjectMap(name = "exam_id")
    private String examId;

    @InjectMap(name = "update_time")
    private String updateTime;

    @InjectMap(name = "factor_id")
    private String factorId;

    @InjectMap(name = "complete_count")
    private int completeCount;

    @InjectMap(name = "child_id")
    private String childId;

    @InjectMap(name = "create_time")
    private String createTime;

    @InjectMap(name = "user_id")
    private long userId;

    @InjectMap(name = "flowers")
    private int flowers;

    @InjectMap(name = "question_count")
    private int questionCount;

    @InjectMap(name = "factor_name")
    private String factorName;

    public String getChildDimensionId() {
        return childDimensionId;
    }

    public void setChildDimensionId(String childDimensionId) {
        this.childDimensionId = childDimensionId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public int getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(int avgScore) {
        this.avgScore = avgScore;
    }

    public String getChildFactorId() {
        return childFactorId;
    }

    public void setChildFactorId(String childFactorId) {
        this.childFactorId = childFactorId;
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

    public String getFactorId() {
        return factorId;
    }

    public void setFactorId(String factorId) {
        this.factorId = factorId;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getFlowers() {
        return flowers;
    }

    public void setFlowers(int flowers) {
        this.flowers = flowers;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public String getFactorName() {
        return factorName;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }
}

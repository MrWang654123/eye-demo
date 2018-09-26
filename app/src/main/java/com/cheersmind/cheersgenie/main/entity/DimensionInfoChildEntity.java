package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 孩子的量表信息
 */
public class DimensionInfoChildEntity implements Serializable{

    @InjectMap(name = "id")
    private String childDimensionId;

    @InjectMap(name = "update_time")
    private String updateTime;

    @InjectMap(name = "status")
    private int status; //0进行中，1个人报告，2班级报告

    @InjectMap(name = "child_id")
    private String childId;

    @InjectMap(name = "create_time")
    private String createTime;

    @InjectMap(name = "dimension_id")
    private String dimensionId;

    @InjectMap(name = "child_topic_id")
    private String childTopicId;

    @InjectMap(name = "user_id")
    private long userId;

    @InjectMap(name = "complete_factor_count")
    private int completeFactorCount;

    @InjectMap(name = "exam_id")
    private String examId;

    @InjectMap(name = "flowers")
    private int flowers;

    @InjectMap(name = "child_exam_id")
    private String childExamId;

    @InjectMap(name = "cost_time")
    private int costTime;

    //话题是否完成
    @InjectMap(name = "is_topic_complete")
    private boolean isTopicComplete;

    //话题量表ID
    @InjectMap(name = "topic_dimension_id")
    private String topicDimensionId;

    //话题ID
    @InjectMap(name = "topic_id")
    private String topicId;


    public String getChildDimensionId() {
        return childDimensionId;
    }

    public void setChildDimensionId(String childDimensionId) {
        this.childDimensionId = childDimensionId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
    }

    public String getChildTopicId() {
        return childTopicId;
    }

    public void setChildTopicId(String childTopicId) {
        this.childTopicId = childTopicId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getCompleteFactorCount() {
        return completeFactorCount;
    }

    public void setCompleteFactorCount(int completeFactorCount) {
        this.completeFactorCount = completeFactorCount;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public int getFlowers() {
        return flowers;
    }

    public void setFlowers(int flowers) {
        this.flowers = flowers;
    }

    public String getChildExamId() {
        return childExamId;
    }

    public void setChildExamId(String childExamId) {
        this.childExamId = childExamId;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public boolean isTopicComplete() {
        return isTopicComplete;
    }

    public void setTopicComplete(boolean topicComplete) {
        isTopicComplete = topicComplete;
    }

    public String getTopicDimensionId() {
        return topicDimensionId;
    }

    public void setTopicDimensionId(String topicDimensionId) {
        this.topicDimensionId = topicDimensionId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}

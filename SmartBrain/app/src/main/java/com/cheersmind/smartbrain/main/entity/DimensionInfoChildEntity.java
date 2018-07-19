package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/28.
 */

public class DimensionInfoChildEntity implements Serializable{
//    "id": "acbdca7a-2f04-46c7-8af2-9cbbabfbc227",
//            "update_time": null,
//            "status": 0,
//            "child_id": "1",
//            "create_time": "2017-10-28T01:19:19.000+0800",
//            "dimension_id": "c07e68da-bafd-42f1-afc8-4a266e4c287e",
//            "child_topic_id": "31b571bc-8524-4276-bbbe-403f3a6a918e",
//            "user_id": 2107950698

//            "complete_factor_count": 1,
//            "exam_id": "1",


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
}

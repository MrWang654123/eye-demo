package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * Created by Administrator on 2017/11/11.
 */

public class FlowerRecordInfoEntity {
//    "id": "d28a6bc1-0f91-4630-a77a-bffcdbc5ae00",
//            "exam_id": "1",
//            "factor_id": "59b86333-1b64-e7ca-2a4b-78acd15c863a",
//            "child_id": "1",
//            "dimension_id": "c07e68da-bafd-42f1-afc8-4a266e4c287e",
//            "create_time": "2017-11-10T23:04:19.000+0800",
//            "user_id": 2109096778,
//            "topic_id": "",
//            "flowers": 4,
//            "type": 2

    @InjectMap(name = "id")
    private String recordId;

    @InjectMap(name = "exam_id")
    private String examId;

    @InjectMap(name = "factor_id")
    private String factorId;

    @InjectMap(name = "child_id")
    private String childId;

    @InjectMap(name = "dimension_id")
    private String dimensionId;

    @InjectMap(name = "create_time")
    private String createTime;

    @InjectMap(name = "user_id")
    private String userId;

    @InjectMap(name = "topic_id")
    private String topicId;

    @InjectMap(name = "flowers")
    private int flowers;

    @InjectMap(name = "type")
    private int type;

    @InjectMap(name = "topic_name")
    private String topicName;

    @InjectMap(name = "factor_name")
    private String factorName;

    @InjectMap(name = "dimension_name")
    private String dimensionName;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getFactorId() {
        return factorId;
    }

    public void setFactorId(String factorId) {
        this.factorId = factorId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public int getFlowers() {
        return flowers;
    }

    public void setFlowers(int flowers) {
        this.flowers = flowers;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getFactorName() {
        return factorName;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }
}

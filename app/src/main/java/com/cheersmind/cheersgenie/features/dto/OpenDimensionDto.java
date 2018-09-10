package com.cheersmind.cheersgenie.features.dto;

/**
 * 开启量表的dto
 */
public class OpenDimensionDto extends BaseDto {

    //孩子ID
    private String childId;

    //测评ID
    private String examId;

    //话题ID
    private String topic;

    //量表ID
    private String dimensionId;

    public OpenDimensionDto() {
    }

    public OpenDimensionDto(String childId, String examId, String topic, String dimensionId) {
        this.childId = childId;
        this.examId = examId;
        this.topic = topic;
        this.dimensionId = dimensionId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
    }
}

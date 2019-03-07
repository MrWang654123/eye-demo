package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 话题dto
 */
public class TopicDto extends BaseDto {

    //场景ID
    private String topicId;

    //孩子ID
    private String childId;

    //孩子测评ID
    private String childExamId;


    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildExamId() {
        return childExamId;
    }

    public void setChildExamId(String childExamId) {
        this.childExamId = childExamId;
    }
}

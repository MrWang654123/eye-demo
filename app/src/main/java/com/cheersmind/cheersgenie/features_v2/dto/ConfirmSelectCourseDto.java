package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features_v2.entity.ChooseCourseEntity;

import java.util.List;

/**
 * 确认选科dto
 */
public class ConfirmSelectCourseDto {

    //孩子Id
    private String childId;

    //孩子测评id
    private String childExamId;

    //选择的课程
    private List<ChooseCourseEntity> items;

    public String getChildExamId() {
        return childExamId;
    }

    public void setChildExamId(String childExamId) {
        this.childExamId = childExamId;
    }

    public List<ChooseCourseEntity> getItems() {
        return items;
    }

    public void setItems(List<ChooseCourseEntity> items) {
        this.items = items;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}

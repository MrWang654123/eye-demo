package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features_v2.entity.CourseGroup;

import java.util.List;

/**
 * 确认选科dto
 */
public class ConfirmSelectCourseDto {

    //孩子Id
    private String childId;

    //孩子测评id
    private String childExamId;

    //选择学科组合的集合
    private List<CourseGroup> items;

    public String getChildExamId() {
        return childExamId;
    }

    public void setChildExamId(String childExamId) {
        this.childExamId = childExamId;
    }

    public List<CourseGroup> getItems() {
        return items;
    }

    public void setItems(List<CourseGroup> items) {
        this.items = items;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}

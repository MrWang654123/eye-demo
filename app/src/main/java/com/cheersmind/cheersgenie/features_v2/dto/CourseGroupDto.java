package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 学科组合dto
 */
public class CourseGroupDto extends BaseDto {

    //孩子ID
    private String child_id;

    //孩子测评ID
    private String child_exam_id;

    public CourseGroupDto(int page, int size) {
        super(page, size);
    }

    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
    }

    public String getChild_exam_id() {
        return child_exam_id;
    }

    public void setChild_exam_id(String child_exam_id) {
        this.child_exam_id = child_exam_id;
    }
}

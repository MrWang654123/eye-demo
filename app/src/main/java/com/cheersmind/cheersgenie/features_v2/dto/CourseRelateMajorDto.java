package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 课程相关专业dto
 */
public class CourseRelateMajorDto extends BaseDto {

    //孩子ID
    private String childId;

    //学科组合id，如：100410051006，必须按顺序的组合，从小到大
    private String subjectGroup;

    //可选，默认是1，1 获取可报考专业， 2 获取有较高要求的专业
    private int type;

    public CourseRelateMajorDto(int page, int size) {
        super(page, size);
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

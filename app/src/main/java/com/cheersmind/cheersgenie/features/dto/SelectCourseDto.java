package com.cheersmind.cheersgenie.features.dto;

/**
 * 选科dto
 */
public class SelectCourseDto extends BaseDto {

    public SelectCourseDto(int page, int size) {
        super(page, size);
    }

    //孩子ID
    private String childId;

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}

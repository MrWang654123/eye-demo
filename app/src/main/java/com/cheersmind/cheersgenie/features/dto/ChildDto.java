package com.cheersmind.cheersgenie.features.dto;

/**
 * 孩子dto
 */
public class ChildDto extends BaseDto {

    //孩子
    private String childId;

    public ChildDto() {
    }

    public ChildDto(int page, int size) {
        super(page, size);
    }

    public ChildDto(String childId) {
        this.childId = childId;
    }

    public ChildDto(int page, int size, String childId) {
        super(page, size);
        this.childId = childId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}

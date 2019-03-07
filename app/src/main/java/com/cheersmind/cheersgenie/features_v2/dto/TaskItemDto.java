package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 任务详情子项dto
 */
public class TaskItemDto extends BaseDto {

    //孩子ID
    private String childId;

    //孩子任务ID
    private String childTaskId;

    public TaskItemDto() {
    }

    public TaskItemDto(int page, int size) {
        super(page, size);
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildTaskId() {
        return childTaskId;
    }

    public void setChildTaskId(String childTaskId) {
        this.childTaskId = childTaskId;
    }
}

package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 测评任务dto
 */
public class ExamTaskDto extends BaseDto {

    //孩子ID
    private String childId;

    //孩子模块ID
    private String childModuleId;

    public ExamTaskDto() {
    }

    public ExamTaskDto(int page, int size) {
        super(page, size);
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildModuleId() {
        return childModuleId;
    }

    public void setChildModuleId(String childModuleId) {
        this.childModuleId = childModuleId;
    }
}

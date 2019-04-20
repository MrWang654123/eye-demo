package com.cheersmind.cheersgenie.features_v2.dto;


import android.support.v4.util.ArrayMap;

import com.cheersmind.cheersgenie.features.dto.BaseDto;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;

/**
 * 添加测评任务dto
 */
public class AddExamTaskDto extends BaseDto {

    //孩子ID
    private String childId;

    //孩子模块ID
    private String childModuleId;

    //任务集合
    private ArrayMap<String, ExamTaskEntity> items;

    public AddExamTaskDto() {
    }

    public AddExamTaskDto(int page, int size) {
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

    public ArrayMap<String, ExamTaskEntity> getItems() {
        return items;
    }

    public void setItems(ArrayMap<String, ExamTaskEntity> items) {
        this.items = items;
    }
}

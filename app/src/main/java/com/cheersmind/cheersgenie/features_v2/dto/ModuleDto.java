package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 模块dto
 */
public class ModuleDto extends BaseDto {

    //孩子ID
    private String childId;

    //可选，默认是1，1发展测评 2生涯规划
    private int type;

    public ModuleDto() {
    }

    public ModuleDto(int page, int size) {
        super(page, size);
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

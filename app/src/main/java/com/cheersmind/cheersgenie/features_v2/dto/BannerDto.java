package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * Banner的dto
 */
public class BannerDto extends BaseDto {

    public BannerDto(int page, int size) {
        super(page, size);
    }

    //孩子ID（便于后台拓展）
    private String childId;

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}

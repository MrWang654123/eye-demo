package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 职业树dto
 */
public class OccupationTreeDto extends BaseDto {

    //职业名称（模糊查询）
    private String occupation_name;

    public String getOccupation_name() {
        return occupation_name;
    }

    public void setOccupation_name(String occupation_name) {
        this.occupation_name = occupation_name;
    }
}

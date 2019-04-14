package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 开设专业dto
 */
public class SetupMajorDto extends BaseDto {

    public SetupMajorDto(int page, int size) {
        super(page, size);
    }

    //院校ID
    private String collegeId;

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }
}

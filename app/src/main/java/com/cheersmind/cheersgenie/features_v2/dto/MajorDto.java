package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 专业树dto
 */
public class MajorDto extends BaseDto {

    //1-专科，2-本科
    private int edu_level;

    //专业名称（模糊查询）
    private String major_name;

    public int getEdu_level() {
        return edu_level;
    }

    public void setEdu_level(int edu_level) {
        this.edu_level = edu_level;
    }

    public String getMajor_name() {
        return major_name;
    }

    public void setMajor_name(String major_name) {
        this.major_name = major_name;
    }
}

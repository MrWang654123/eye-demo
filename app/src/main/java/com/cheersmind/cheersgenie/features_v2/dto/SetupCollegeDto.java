package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 开设院校dto
 */
public class SetupCollegeDto extends BaseDto {

    public SetupCollegeDto(int page, int size) {
        super(page, size);
    }

    //专业代码
    private String major_code;

    //地区编码
    private String state;

    //学校类别 //暂时没使用
    private String institute_type;

    public String getMajor_code() {
        return major_code;
    }

    public void setMajor_code(String major_code) {
        this.major_code = major_code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInstitute_type() {
        return institute_type;
    }

    public void setInstitute_type(String institute_type) {
        this.institute_type = institute_type;
    }
}

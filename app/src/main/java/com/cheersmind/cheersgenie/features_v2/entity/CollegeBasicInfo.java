package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 院校
 */
public class CollegeBasicInfo implements Serializable {

    //公立或者私立
    @InjectMap(name = "public_or_private")
    private String public_or_private;

    //院校所属（例如：教育部直属）
    @InjectMap(name = "china_belong_to")
    private String china_belong_to;

    //院校特色（例如："985", "双一流", "C9"）
    @InjectMap(name = "institute_quality")
    private List<String> institute_quality;

    //院校类别
    @InjectMap(name = "institute_type")
    private String institute_type;


    public String getPublic_or_private() {
        return public_or_private;
    }

    public void setPublic_or_private(String public_or_private) {
        this.public_or_private = public_or_private;
    }

    public String getChina_belong_to() {
        return china_belong_to;
    }

    public void setChina_belong_to(String china_belong_to) {
        this.china_belong_to = china_belong_to;
    }

    public List<String> getInstitute_quality() {
        return institute_quality;
    }

    public void setInstitute_quality(List<String> institute_quality) {
        this.institute_quality = institute_quality;
    }

    public String getInstitute_type() {
        return institute_type;
    }

    public void setInstitute_type(String institute_type) {
        this.institute_type = institute_type;
    }

}

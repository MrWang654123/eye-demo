package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 相关专业比率
 */
public class MajorRatio {

    //选科组合id，必须从小到大按顺序拼接
    @InjectMap(name = "subject_group")
    private String subject_group;

    //可报考率，只针对本科
    @InjectMap(name = "rate")
    private Double rate;

    //有选科要求的，可报考率
    @InjectMap(name = "require_rate")
    private Double require_rate;

    public String getSubject_group() {
        return subject_group;
    }

    public void setSubject_group(String subject_group) {
        this.subject_group = subject_group;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getRequire_rate() {
        return require_rate;
    }

    public void setRequire_rate(Double require_rate) {
        this.require_rate = require_rate;
    }
}

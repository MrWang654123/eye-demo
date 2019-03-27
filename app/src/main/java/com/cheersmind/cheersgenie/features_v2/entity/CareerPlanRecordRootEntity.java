package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 生涯规划档案响应的根对象
 */
public class CareerPlanRecordRootEntity {

    //高考选科档案
    @InjectMap(name = "subject_archive")
    private SelectCourseRecord subject_archive;

    //职业档案
    @InjectMap(name = "occupation_archive")
    private OccupationRecord occupation_archive;

    public SelectCourseRecord getSubject_archive() {
        return subject_archive;
    }

    public void setSubject_archive(SelectCourseRecord subject_archive) {
        this.subject_archive = subject_archive;
    }

    public OccupationRecord getOccupation_archive() {
        return occupation_archive;
    }

    public void setOccupation_archive(OccupationRecord occupation_archive) {
        this.occupation_archive = occupation_archive;
    }
}

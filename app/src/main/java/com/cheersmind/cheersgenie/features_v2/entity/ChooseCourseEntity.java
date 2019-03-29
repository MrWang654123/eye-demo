package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 确认选课
 */
public class ChooseCourseEntity implements Serializable {

    //学科编码
    @InjectMap(name = "subject_code")
    private int subject_code;

    //学科名称
    @InjectMap(name = "subject_name")
    private String subject_name;

    //学科类型 1理科，2文科
    @InjectMap(name = "subject_type")
    private String subject_type;

    //是否选中
    private boolean isSelected;

    public int getSubject_code() {
        return subject_code;
    }

    public void setSubject_code(int subject_code) {
        this.subject_code = subject_code;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getSubject_type() {
        return subject_type;
    }

    public void setSubject_type(String subject_type) {
        this.subject_type = subject_type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

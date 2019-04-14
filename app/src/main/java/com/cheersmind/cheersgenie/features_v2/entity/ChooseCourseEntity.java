package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 确认选科
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
    private Integer subject_type;

    //图标
    @InjectMap(name = "subject_icon")
    private String subject_icon;

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

    public Integer getSubject_type() {
        return subject_type;
    }

    public void setSubject_type(Integer subject_type) {
        this.subject_type = subject_type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSubject_icon() {
        return subject_icon;
    }

    public void setSubject_icon(String subject_icon) {
        this.subject_icon = subject_icon;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(null == obj) {
            return false;
        }
        if(this.getClass() != obj.getClass()) {
            return false;
        }

        ChooseCourseEntity course = (ChooseCourseEntity) obj;
        return this.subject_name.equals(course.subject_name);
    }

    @Override
    public int hashCode() {
        int result = subject_code;
        result = 31 * result + (subject_name != null ? subject_name.hashCode() : 0);
        result = 31 * result + (subject_type != null ? subject_type.hashCode() : 0);
        result = 31 * result + (subject_icon != null ? subject_icon.hashCode() : 0);
//        result = 31 * result + (isSelected ? 1 : 0);//忽略选中状态
        return result;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features_v2.adapter.MajorTreeRecyclerAdapter;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 专业
 */
public class MajorItem implements MultiItemEntity, Serializable {

    //专业代码
    @InjectMap(name = "major_code")
    private String major_code;

    //专业名称
    @InjectMap(name = "major_name")
    private String major_name;

    //专业评级
    @InjectMap(name = "assessment_level")
    private String assessment_level;

    //特指名称
    @InjectMap(name = "special_name")
    private String special_name;

    //高要求学科
    @InjectMap(name = "subjects_required")
    private String subjects_required;

    //布局类型
    private int itemType = MajorTreeRecyclerAdapter.LAYOUT_TYPE_LEVEL2;

    //最里层的子项是否是兄弟中的最后一个
    private boolean isLastInMaxLevel;

    public String getMajor_code() {
        return major_code;
    }

    public void setMajor_code(String major_code) {
        this.major_code = major_code;
    }

    public String getMajor_name() {
        return major_name;
    }

    public void setMajor_name(String major_name) {
        this.major_name = major_name;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isLastInMaxLevel() {
        return isLastInMaxLevel;
    }

    public void setLastInMaxLevel(boolean lastInMaxLevel) {
        isLastInMaxLevel = lastInMaxLevel;
    }

    public String getAssessment_level() {
        return assessment_level;
    }

    public void setAssessment_level(String assessment_level) {
        this.assessment_level = assessment_level;
    }

    public String getSpecial_name() {
        return special_name;
    }

    public void setSpecial_name(String special_name) {
        this.special_name = special_name;
    }

    public String getSubjects_required() {
        return subjects_required;
    }

    public void setSubjects_required(String subjects_required) {
        this.subjects_required = subjects_required;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 学科组合
 */
public class CourseGroup implements Serializable, MultiItemEntity {

    @InjectMap(name = "subject_group")
    private String subjectGroup;//学科组合id

    @InjectMap(name = "subject_group_name")
    private String subjectGroupName; //学科组合名称

    @InjectMap(name = "rate")
    private Double rate; //可报考专业覆盖率

    @InjectMap(name = "require_rate")
    private Double requireRate; //有要求专业覆盖率

    @InjectMap(name = "follow_rate")
    private Double followRate; //意向专业覆盖率

    @InjectMap(name = "ability_rate")
    private Double ability_rate; //匹配度

    private int index;

    private boolean last;

    //是否选中
    private boolean selected;

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public String getSubjectGroupName() {
        return subjectGroupName;
    }

    public void setSubjectGroupName(String subjectGroupName) {
        this.subjectGroupName = subjectGroupName;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getRequireRate() {
        return requireRate;
    }

    public void setRequireRate(Double requireRate) {
        this.requireRate = requireRate;
    }

    public Double getFollowRate() {
        return followRate;
    }

    public void setFollowRate(Double followRate) {
        this.followRate = followRate;
    }

    public Double getAbility_rate() {
        return ability_rate;
    }

    public void setAbility_rate(Double ability_rate) {
        this.ability_rate = ability_rate;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public CourseGroup setItemType(int itemType) {
        this.itemType = itemType;
        return this;
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

        CourseGroup courseGroup = (CourseGroup) obj;
        return this.subjectGroup.equals(courseGroup.subjectGroup);
    }

    @Override
    public int hashCode() {
        int result = (subjectGroup != null ? subjectGroup.hashCode() : 0);
        result = 31 * result + (subjectGroupName != null ? subjectGroupName.hashCode() : 0);
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        result = 31 * result + (requireRate != null ? requireRate.hashCode() : 0);
        result = 31 * result + (followRate != null ? followRate.hashCode() : 0);
        result = 31 * result + (ability_rate != null ? ability_rate.hashCode() : 0);
//        result = 31 * result + (isSelected ? 1 : 0);//忽略选中状态
        return result;
    }

}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 行业详情
 */
public class OccupationDetail implements Serializable {

    //行业ID
    @InjectMap(name = "occupation_id")
    private long occupation_id;

    //行业名称
    @InjectMap(name = "occupation_name")
    private String occupation_name;

    //所属领域
    @InjectMap(name = "realm")
    private String realm;

    //所属门类
    @InjectMap(name = "category")
    private String category;

    //行业介绍
    @InjectMap(name = "introduces")
    private List<OccupationIntroduce> introduces;

    //对口专业
    @InjectMap(name = "majors")
    private List<MajorItem> suitMajors;

    //是否关注
    @InjectMap(name = "is_follow")
    private boolean follow;


    public long getOccupation_id() {
        return occupation_id;
    }

    public void setOccupation_id(long occupation_id) {
        this.occupation_id = occupation_id;
    }

    public String getOccupation_name() {
        return occupation_name;
    }

    public void setOccupation_name(String occupation_name) {
        this.occupation_name = occupation_name;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<OccupationIntroduce> getIntroduces() {
        return introduces;
    }

    public void setIntroduces(List<OccupationIntroduce> introduces) {
        this.introduces = introduces;
    }

    public List<MajorItem> getSuitMajors() {
        return suitMajors;
    }

    public void setSuitMajors(List<MajorItem> suitMajors) {
        this.suitMajors = suitMajors;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}

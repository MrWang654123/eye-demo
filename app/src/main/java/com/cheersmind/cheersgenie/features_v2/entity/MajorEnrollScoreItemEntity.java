package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 院校录取分数项
 */
public class MajorEnrollScoreItemEntity implements MultiItemEntity, Serializable {

    //平均分
    @InjectMap(name = "average_score")
    private int average_score;

    //批次后缀
    @InjectMap(name = "batch")
    private String batch;

    //批次前缀
    @InjectMap(name = "bkcc")
    private String bkcc;

    //最高分
    @InjectMap(name = "high_score")
    private int high_score;

    //文理科
    @InjectMap(name = "kind")
    private String kind;

    //最低分
    @InjectMap(name = "low_score")
    private int low_score;

    //最低分排名
    @InjectMap(name = "low_wc")
    private int low_wc;

    //录取数
    @InjectMap(name = "luqu_num")
    private int luqu_num;

    //专业名称
    @InjectMap(name = "major")
    private String major;

    //标记？？
    @InjectMap(name = "mark")
    private String mark;

    //省控线？？
    @InjectMap(name = "province_score")
    private int province_score;

    //学校名称
    @InjectMap(name = "school")
    private String school;

    //年份
    @InjectMap(name = "year")
    private String year;


    public int getAverage_score() {
        return average_score;
    }

    public void setAverage_score(int average_score) {
        this.average_score = average_score;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getBkcc() {
        return bkcc;
    }

    public void setBkcc(String bkcc) {
        this.bkcc = bkcc;
    }

    public int getHigh_score() {
        return high_score;
    }

    public void setHigh_score(int high_score) {
        this.high_score = high_score;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getLow_score() {
        return low_score;
    }

    public void setLow_score(int low_score) {
        this.low_score = low_score;
    }

    public int getLow_wc() {
        return low_wc;
    }

    public void setLow_wc(int low_wc) {
        this.low_wc = low_wc;
    }

    public int getLuqu_num() {
        return luqu_num;
    }

    public void setLuqu_num(int luqu_num) {
        this.luqu_num = luqu_num;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getProvince_score() {
        return province_score;
    }

    public void setProvince_score(int province_score) {
        this.province_score = province_score;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}

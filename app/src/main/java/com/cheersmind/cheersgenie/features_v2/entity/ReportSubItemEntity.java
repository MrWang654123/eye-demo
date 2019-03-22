package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 报告子项
 */
public class ReportSubItemEntity implements MultiItemEntity, Serializable {

//    {
//        "item_name":"string",       //项目名称
//            "item_id":"string",         //项目ID
//            "result":"string",          //测评结果
//            "score":50.5,               //测评得分（原始分或者T分数）
//            "rank":90,                  //超过%90的用户
//            "description":"string"      //项目描述
//    }

    //项目名称
    @InjectMap(name = "item_name")
    private String item_name;

    //项目ID
    @InjectMap(name = "item_id")
    private String item_id;

    //测评结果
    @InjectMap(name = "result")
    private String result;

    //测评得分（原始分或者T分数）
    @InjectMap(name = "score")
    private double score;

    //超过%90的用户
    @InjectMap(name = "rank")
    private double rank;

    //项目描述
    @InjectMap(name = "description")
    private String description;

    //排名
    @InjectMap(name = "sort")
    private int sort;

    //是否展开
    private boolean expand;

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}

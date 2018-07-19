package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

/**
 * Created by Administrator on 2017/11/11.
 */

public class FactorRankInfoEntity {
//    "child_name": "1",
//            "rank": 0,
//            "cost_time": 50000,
//            "child_id": "1",
//            "avg_score": 2

    @InjectMap(name = "child_name")
    private String childName;

    @InjectMap(name = "rank")
    private int rank;

    @InjectMap(name = "cost_time")
    private int costTime;

    @InjectMap(name = "child_id")
    private String childId;

    @InjectMap(name = "avg_score")
    private int avgScore;

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public int getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(int avgScore) {
        this.avgScore = avgScore;
    }
}

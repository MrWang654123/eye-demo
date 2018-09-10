package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 历史报告
 */
public class HistoryReportRootEntity {

    //报告项
    @InjectMap(name = "items")
    private List<HistoryReportItemEntity> items;

    //对比范围名称，例如：全国
    @InjectMap(name = "sample_name")
    private String sampleName;

    //分数趋势
    @InjectMap(name = "trend_score")
    private String trendScore;


    public List<HistoryReportItemEntity> getItems() {
        return items;
    }

    public void setItems(List<HistoryReportItemEntity> items) {
        this.items = items;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getTrendScore() {
        return trendScore;
    }

    public void setTrendScore(String trendScore) {
        this.trendScore = trendScore;
    }

}

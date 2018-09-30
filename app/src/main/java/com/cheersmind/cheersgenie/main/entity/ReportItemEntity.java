package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/6/27.
 */

public class ReportItemEntity implements Serializable {

//    "isTopic": true,
//            "report_result": null,
//            "score_type": 0,
//            "items": [],
//            "min_score": 0,
//            "chart_item_id": "850023ae-dfca-41c4-ab81-f1165fe2dc04",
//            "chart_show_item_name": false,
//            "chart_type": 2,
//            "max_score": 10,
//            "chart_description": "原始分雷达图",
//            "chart_item_name": "我的学习习惯"

    //是否是话题报告
    @InjectMap(name = "is_topic")
    private boolean isTopic;

    //报告结果
    @InjectMap(name = "report_result")
    private ReportResultEntity reportResult;

    //分数类型，1-T分数，2-百分位分数，3-原始分
    @InjectMap(name = "score_type")
    private int scoreType;

    @InjectMap(name = "items")
    private List<ReportFactorEntity> items;

    //最小分数
    @InjectMap(name = "min_score")
    private int minScore;

    //当前chart的主题或量表ID
    @InjectMap(name = "chart_item_id")
    private String chartItemId;

    @InjectMap(name = "chart_show_item_name")
    private boolean chartShowItemName;

    //图表类型，1-雷达图，2-曲线图，3-柱状图，4-条状图
    @InjectMap(name = "chart_type")
    private int chartType;

    //最大分数
    @InjectMap(name = "max_score")
    private int maxScore;

    //说明
    @InjectMap(name = "chart_description")
    private String chartDescription;

    //当前chart的主题或量表名称
    @InjectMap(name = "chart_item_name")
    private String chartItemName;

    public boolean getTopic() {
        return isTopic;
    }

    public void setTopic(boolean topic) {
        this.isTopic = topic;
    }

    public ReportResultEntity getReportResult() {
        return reportResult;
    }

    public void setReportResult(ReportResultEntity reportResult) {
        this.reportResult = reportResult;
    }

    public int getScoreType() {
        return scoreType;
    }

    public void setScoreType(int scoreType) {
        this.scoreType = scoreType;
    }

    public List<ReportFactorEntity> getItems() {
        return items;
    }

    public void setItems(List<ReportFactorEntity> items) {
        this.items = items;
    }

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

    public String getChartItemId() {
        return chartItemId;
    }

    public void setChartItemId(String chartItemId) {
        this.chartItemId = chartItemId;
    }

    public boolean isChartShowItemName() {
        return chartShowItemName;
    }

    public void setChartShowItemName(boolean chartShowItemName) {
        this.chartShowItemName = chartShowItemName;
    }

    public int getChartType() {
        return chartType;
    }

    public void setChartType(int chartType) {
        this.chartType = chartType;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public String getChartDescription() {
        return chartDescription;
    }

    public void setChartDescription(String chartDescription) {
        this.chartDescription = chartDescription;
    }

    public String getChartItemName() {
        return chartItemName;
    }

    public void setChartItemName(String chartItemName) {
        this.chartItemName = chartItemName;
    }
}

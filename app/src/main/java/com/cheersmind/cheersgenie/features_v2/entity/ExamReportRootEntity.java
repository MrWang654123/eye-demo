package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 测评报告的响应根结果
 */
public class ExamReportRootEntity implements Serializable, MultiItemEntity {

    //对比样本名称
    @InjectMap(name = "compare_name")
    private String compareName;

    //标题
    @InjectMap(name = "title")
    private String title;

    //描述
    @InjectMap(name = "description")
    private String description;

    //测评结果（场景下不一定有总体测评结果）
    @InjectMap(name = "result")
    private String result;

    //测评得分（原始分或者T分数，场景下不一定有总体得分）
    @InjectMap(name = "score")
    private double score;

    //子维度测评结果的集合（对于场景下没有设定总体结果的情况，用这个字段显示）
    @InjectMap(name = "sub_result")
    private List<String> sub_result;

    //排名，例如超过%90的用户（测评得分是原始分就没有排名）
    @InjectMap(name = "rank")
    private double rank;

    //评价（测评得分是原始分就没有评价）
    @InjectMap(name = "appraisal")
    private String appraisal;

    //图表数据
    @InjectMap(name = "chart_datas")
    private List<ReportItemEntity> chartDatas;

    //子项集合
    @InjectMap(name = "sub_items")
    private List<ReportSubItemEntity> subItems;

    //图表类型（目前用于区分MBTI量表，等于5）
    @InjectMap(name = "chart_type")
    private int chart_type;

    //MBTI的额外结果（典型形象）
    @InjectMap(name = "vice_result")
    private String vice_result;

    //MBTI图表数据
    @InjectMap(name = "mbti_date")
    private List<ExamMbtiData> mbtiData;

    //推荐的ACT职业分类
    @InjectMap(name = "recommend")
    private List<ActType> recommendActType;

    //是否是话题
    private boolean topic;

    public boolean isTopic() {
        return topic;
    }

    public void setTopic(boolean topic) {
        this.topic = topic;
    }

    public String getCompareName() {
        return compareName;
    }

    public void setCompareName(String compareName) {
        this.compareName = compareName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getSub_result() {
        return sub_result;
    }

    public void setSub_result(List<String> sub_result) {
        this.sub_result = sub_result;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public String getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(String appraisal) {
        this.appraisal = appraisal;
    }

    public List<ReportItemEntity> getChartDatas() {
        return chartDatas;
    }

    public void setChartDatas(List<ReportItemEntity> chartDatas) {
        this.chartDatas = chartDatas;
    }

    public List<ReportSubItemEntity> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<ReportSubItemEntity> subItems) {
        this.subItems = subItems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getChart_type() {
        return chart_type;
    }

    public void setChart_type(int chart_type) {
        this.chart_type = chart_type;
    }

    public String getVice_result() {
        return vice_result;
    }

    public void setVice_result(String vice_result) {
        this.vice_result = vice_result;
    }

    public List<ExamMbtiData> getMbtiData() {
        return mbtiData;
    }

    public void setMbtiData(List<ExamMbtiData> mbtiData) {
        this.mbtiData = mbtiData;
    }

    public List<ActType> getRecommendActType() {
        return recommendActType;
    }

    public void setRecommendActType(List<ActType> recommendActType) {
        this.recommendActType = recommendActType;
    }

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public ExamReportRootEntity setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }
}

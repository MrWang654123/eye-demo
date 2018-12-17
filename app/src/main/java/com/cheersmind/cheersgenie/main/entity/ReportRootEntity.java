package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/6/27.
 */

public class ReportRootEntity implements Serializable {

    //图表数据
    @InjectMap(name = "chart_datas")
    private List<ReportItemEntity> chartDatas;

    //对比范围名称
    @InjectMap(name = "compare_name")
    private String compareName;

    @InjectMap(name = "report_results")
    private List<ReportResultEntity> reportResults;

    //话题名称
    @InjectMap(name = "topic_name")
    private String topicName;

    public List<ReportItemEntity> getChartDatas() {
        return chartDatas;
    }

    public void setChartDatas(List<ReportItemEntity> chartDatas) {
        this.chartDatas = chartDatas;
    }

    public String getCompareName() {
        return compareName;
    }

    public void setCompareName(String compareName) {
        this.compareName = compareName;
    }

    public List<ReportResultEntity> getReportResults() {
        return reportResults;
    }

    public void setReportResults(List<ReportResultEntity> reportResults) {
        this.reportResults = reportResults;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}

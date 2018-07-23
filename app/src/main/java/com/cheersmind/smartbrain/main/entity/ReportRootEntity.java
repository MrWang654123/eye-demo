package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/6/27.
 */

public class ReportRootEntity implements Serializable {

    @InjectMap(name = "chart_datas")
    private List<ReportItemEntity> chartDatas;

    @InjectMap(name = "compare_name")
    private String compareName;

    @InjectMap(name = "report_results")
    private List<ReportResultEntity> reportResults;

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
}

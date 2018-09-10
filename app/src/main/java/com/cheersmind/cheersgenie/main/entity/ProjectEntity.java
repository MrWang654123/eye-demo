package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/28.
 */

public class ProjectEntity extends DataSupport implements Serializable {

//    "exam_id": "1",
//            "exam_name": "高中测评项目",
//            "project_id": "be30fc10-552e-438c-815d-a104fd6e22a6",
//            "start_time": "2017-10-23",
//            "end_time": "2017-12-23",
//            "is_report": 0,
//            "project_name": "高中测评项目"

    @InjectMap(name = "exam_id")
    private String examId;

    @InjectMap(name = "exam_name")
    private String examName;

    @InjectMap(name = "project_id")
    private String projectId;

    @InjectMap(name = "start_time")
    private String startTime;

    @InjectMap(name = "end_time")
    private String endTime;

    @InjectMap(name = "is_report")
    private int isReport;

    @InjectMap(name = "project_name")
    private String projectName;

    @InjectMap(name = "compare_data")
    private String compareData;

    @InjectMap(name = "is_show_report")
    private int isShowReport;//0没有项目报告，1有项目报告

    private int curSelect;//当前选择项目，首页切换项目使用

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getIsReport() {
        return isReport;
    }

    public void setIsReport(int isReport) {
        this.isReport = isReport;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getCurSelect() {
        return curSelect;
    }

    public void setCurSelect(int curSelect) {
        this.curSelect = curSelect;
    }

    public String getCompareData() {
        return compareData;
    }

    public void setCompareData(String compareData) {
        this.compareData = compareData;
    }

    public int getIsShowReport() {
        return isShowReport;
    }

    public void setIsShowReport(int isShowReport) {
        this.isShowReport = isShowReport;
    }
}

package com.cheersmind.cheersgenie.features.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features.adapter.HistorySeminarRecyclerAdapter;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 测评
 */
public class ExamEntity implements MultiItemEntity, Serializable {

    //测评ID
    @InjectMap(name = "exam_id")
    private String examId;

    //测评名称
    @InjectMap(name = "exam_name")
    private String examName;

    //专题id
    @InjectMap(name = "seminar_id")
    private String seminarId;

    //专题名称
    @InjectMap(name = "seminar_name")
    private String seminarName;

    //开始时间
    @InjectMap(name = "start_time")
    private String startTime;

    //结束时间
    @InjectMap(name = "end_time")
    private String endTime;

    //状态
    @InjectMap(name = "status")
    private int status;

    //孩子测评状态
    @InjectMap(name = "child_exam_status")
    private int childExamStatus;

    //该测评下中量表数量
    @InjectMap(name = "total_dimensions")
    private int totalDimensions;

    //已经完成的量表数量
    @InjectMap(name = "complete_dimensions")
    private int completeDimensions;

    //话题数量
    @InjectMap(name = "total_topics")
    private int totalTopics;

    //完成的话题数量
    @InjectMap(name = "complete_topics")
    private int completeTopics;

    //话题集合
    @InjectMap(name = "items")
    private List<TopicInfoEntity> topics;

    //是否是专题中的第一个测评
    private boolean isFirstInSeminar;
    //是否是专题中的最后一个测评
    private boolean isLastInSeminar;

    //布局类型
    private int itemType = -1;


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

    public String getSeminarId() {
        return seminarId;
    }

    public void setSeminarId(String seminarId) {
        this.seminarId = seminarId;
    }

    public String getSeminarName() {
        return seminarName;
    }

    public void setSeminarName(String seminarName) {
        this.seminarName = seminarName;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotalDimensions() {
        return totalDimensions;
    }

    public void setTotalDimensions(int totalDimensions) {
        this.totalDimensions = totalDimensions;
    }

    public int getCompleteDimensions() {
        return completeDimensions;
    }

    public void setCompleteDimensions(int completeDimensions) {
        this.completeDimensions = completeDimensions;
    }

    public List<TopicInfoEntity> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicInfoEntity> topics) {
        this.topics = topics;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getChildExamStatus() {
        return childExamStatus;
    }

    public void setChildExamStatus(int childExamStatus) {
        this.childExamStatus = childExamStatus;
    }

    public int getTotalTopics() {
        return totalTopics;
    }

    public void setTotalTopics(int totalTopics) {
        this.totalTopics = totalTopics;
    }

    public int getCompleteTopics() {
        return completeTopics;
    }

    public void setCompleteTopics(int completeTopics) {
        this.completeTopics = completeTopics;
    }

    public boolean isLastInSeminar() {
        return isLastInSeminar;
    }

    public void setLastInSeminar(boolean lastInSeminar) {
        isLastInSeminar = lastInSeminar;
    }

    public boolean isFirstInSeminar() {
        return isFirstInSeminar;
    }

    public void setFirstInSeminar(boolean firstInSeminar) {
        isFirstInSeminar = firstInSeminar;
    }
}


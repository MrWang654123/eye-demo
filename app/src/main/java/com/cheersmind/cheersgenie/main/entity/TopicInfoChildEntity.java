package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 孩子的话题（场景）
 */
public class TopicInfoChildEntity extends DataSupport implements Serializable{

    @InjectMap(name = "id")
    private String childTopicId;

    @InjectMap(name = "exam_id")
    private String examId;

    @InjectMap(name = "user_id")
    private String userId;

    @InjectMap(name = "child_id")
    private String childId;

    @InjectMap(name = "child_exam_id")
    private String childExamId;

    @InjectMap(name = "topic_id")
    private String topicId;

    @InjectMap(name = "orderby")
    private int orderby;

    @InjectMap(name = "status")
    private int status;

    @InjectMap(name = "create_time")
    private String createTime;

    @InjectMap(name = "update_time")
    private String updateTime;

    @JsonProperty
    @InjectMap(name = "followed")
    private boolean followed;

    public String getChildTopicId() {
        return childTopicId;
    }

    public void setChildTopicId(String childTopicId) {
        this.childTopicId = childTopicId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getOrderby() {
        return orderby;
    }

    public void setOrderby(int orderby) {
        this.orderby = orderby;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public String getChildExamId() {
        return childExamId;
    }

    public void setChildExamId(String childExamId) {
        this.childExamId = childExamId;
    }
}

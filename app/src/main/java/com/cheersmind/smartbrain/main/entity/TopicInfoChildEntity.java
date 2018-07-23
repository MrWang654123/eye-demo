package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/2.
 */

public class TopicInfoChildEntity extends DataSupport implements Serializable{
//    "id": "31b571bc-8524-4276-bbbe-403f3a6a918e",
//                "exam_id": "1",
//                "update_time": "2017-10-26T23:23:25.000+0800",
//                "status": 0,
//                "child_id": "1",
//                "create_time": "2017-10-26T23:21:12.000+0800",
//                "orderby": 0,
//                "user_id": 2107950698,
//                "topic_id": "f0b5d098-38bd-4d4e-bfbc-15fe342cb267",
//                "followed": true

    @InjectMap(name = "id")
    private String childTopicId;

    @InjectMap(name = "exam_id")
    private String examId;

    @InjectMap(name = "update_time")
    private String updateTime;

    @InjectMap(name = "status")
    private int status;

    @InjectMap(name = "child_id")
    private String childId;

    @InjectMap(name = "create_time")
    private String createTime;

    @InjectMap(name = "orderby")
    private int orderby;

    @InjectMap(name = "user_id")
    private String userId;

    @InjectMap(name = "topic_id")
    private String topicId;

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
}

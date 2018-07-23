package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */

public class TopicInfoEntity extends DataSupport implements Serializable{

//    "child_topic": {
//        "id": "31b571bc-8524-4276-bbbe-403f3a6a918e",
//                "exam_id": "1",
//                "update_time": "2017-10-26T23:23:25.000+0800",
//                "status": 0,
//                "child_id": "1",
//                "create_time": "2017-10-26T23:21:12.000+0800",
//                "orderby": 0,
//                "user_id": 2107950698,
//                "topic_id": "f0b5d098-38bd-4d4e-bfbc-15fe342cb267",
//                "followed": true
//    },
//            "icon": "1",
//            "topic_name": "我的家庭",
//            "orderby": 1,
//            "topic_id": "f0b5d098-38bd-4d4e-bfbc-15fe342cb267",
//            "deccription": "我的家庭",
//            "algorithm": "1"

    @InjectMap(name = "topic_id")
    private String topicId;

    @InjectMap(name = "topic_name")
    private String topicName;

    @InjectMap(name = "icon")
    private String icon;

    @InjectMap(name = "deccription")
    private String deccription;

    @InjectMap(name = "orderby")
    private int orderby;

    @InjectMap(name = "child_topic")
    private TopicInfoChildEntity childTopic;

    @InjectMap(name = "pre_topic")
    private String preTopic;

    @InjectMap(name = "is_locked")
    private int isLocked;//0已解锁，1锁定

    @InjectMap(name = "exam_id")
    private String examId;

    @InjectMap(name = "dimensions")
    private List<DimensionInfoEntity> dimensions;

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDeccription() {
        return deccription;
    }

    public void setDeccription(String deccription) {
        this.deccription = deccription;
    }

    public int getOrderby() {
        return orderby;
    }

    public void setOrderby(int orderby) {
        this.orderby = orderby;
    }

    public TopicInfoChildEntity getChildTopic() {
        return childTopic;
    }

    public void setChildTopic(TopicInfoChildEntity childTopic) {
        this.childTopic = childTopic;
    }

    public String getPreTopic() {
        return preTopic;
    }

    public void setPreTopic(String preTopic) {
        this.preTopic = preTopic;
    }

    public int getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(int isLocked) {
        this.isLocked = isLocked;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public List<DimensionInfoEntity> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<DimensionInfoEntity> dimensions) {
        this.dimensions = dimensions;
    }
}

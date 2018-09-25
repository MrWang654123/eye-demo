package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 话题（场景）信息
 */
public class TopicInfo extends DataSupport implements Serializable {

    //话题id
    @InjectMap(name = "topic_id")
    private String topicId;

    //话题名称
    @InjectMap(name = "topic_name")
    private String topicName;

    //量表id
    @InjectMap(name = "dimension_id")
    private String dimensionId;

    //量表名称
    @InjectMap(name = "dimension_name")
    private String dimensionName;


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

    public String getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

}

package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 话题（场景）信息
 */
public class TopicInfo extends DataSupport implements Serializable {

    //id
    @InjectMap(name = "topic_id")
    private String topic_id;

    //名称
    @InjectMap(name = "topic_name")
    private String topicName;

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}

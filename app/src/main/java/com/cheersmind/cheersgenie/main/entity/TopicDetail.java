package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 话题详细
 */
public class TopicDetail {

    //话题ID
    @InjectMap(name = "topic_id")
    private String topicId;

    //话题名称
    @InjectMap(name = "topic_name")
    private String topicName;

    //图片
    @InjectMap(name = "icon")
    private String icon;

    //使用数量
    @InjectMap(name = "use_count")
    private int useCount;

    //描述信息
    @InjectMap(name = "description")
    private String description;


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

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

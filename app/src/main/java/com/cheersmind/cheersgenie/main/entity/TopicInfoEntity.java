package com.cheersmind.cheersgenie.main.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * 话题（场景）
 */
public class TopicInfoEntity extends AbstractExpandableItem<DimensionInfoEntity> implements MultiItemEntity, Serializable {

    @InjectMap(name = "topic_id")
    private String topicId;

    @InjectMap(name = "topic_name")
    private String topicName;

    @InjectMap(name = "icon")
    private String icon;

    @InjectMap(name = "algorithm")
    private String algorithm;

    @InjectMap(name = "deccription")
    private String deccription;

    @InjectMap(name = "orderby")
    private int orderby;

    @InjectMap(name = "use_count")
    private int useCount;

    @InjectMap(name = "dimension_count")
    private int dimensionCount;

    @InjectMap(name = "appraisal_type")
    private int appraisalType;

    @InjectMap(name = "is_dynamic_dimensions")
    private int isDynamicDimensions;

    @InjectMap(name = "pre_topic")
    private String preTopic;

    @InjectMap(name = "exam_id")
    private String examId;

    @InjectMap(name = "start_time")
    private String  startTime;

    @InjectMap(name = "end_time")
    private String  endTime;

    @InjectMap(name = "is_locked")
    private int isLocked;//0已解锁，1锁定

    @InjectMap(name = "dimensions")
    private List<DimensionInfoEntity> dimensions;

    @InjectMap(name = "child_topic")
    private TopicInfoChildEntity childTopic;


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

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public int getDimensionCount() {
        return dimensionCount;
    }

    public void setDimensionCount(int dimensionCount) {
        this.dimensionCount = dimensionCount;
    }

    public int getAppraisalType() {
        return appraisalType;
    }

    public void setAppraisalType(int appraisalType) {
        this.appraisalType = appraisalType;
    }

    public int getIsDynamicDimensions() {
        return isDynamicDimensions;
    }

    public void setIsDynamicDimensions(int isDynamicDimensions) {
        this.isDynamicDimensions = isDynamicDimensions;
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

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}

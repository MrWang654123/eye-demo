package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 简单的量表结果
 */
public class SimpleDimensionResult extends BaseRecordItem {

    @InjectMap(name = "dimension_id")
    private String dimension_id;

    @InjectMap(name = "dimension_name")
    private String dimension_name;

    //测评评价，如果没有值，表示该量表未完成
    @InjectMap(name = "appraisal")
    private String appraisal;

    @InjectMap(name = "topic_id")
    private String topic_id;

    //是否完成
    @InjectMap(name = "finish")
    private boolean finish;

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public SimpleDimensionResult setItemType(int itemType) {
        this.itemType = itemType;
        return this;
    }

    public String getDimension_id() {
        return dimension_id;
    }

    public void setDimension_id(String dimension_id) {
        this.dimension_id = dimension_id;
    }

    public String getDimension_name() {
        return dimension_name;
    }

    public void setDimension_name(String dimension_name) {
        this.dimension_name = dimension_name;
    }

    public String getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(String appraisal) {
        this.appraisal = appraisal;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    @Override
    public boolean isFinish() {
        return finish;
    }

    @Override
    public void setFinish(boolean finish) {
        this.finish = finish;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 职业档案子项
 */
public class OccupationRecordItem extends BaseRecordItem {

    //话题ID
    @InjectMap(name = "topic_id")
    private String topic_id;

    //量表ID
    @InjectMap(name = "dimension_id")
    private String dimension_id;

    //量表名称
    @InjectMap(name = "dimension_name")
    private String dimension_name;

    //是否完成
    @InjectMap(name = "finish")
    private boolean finish;

    //评价
    @InjectMap(name = "appraisal")
    private String appraisal;

    //推荐选科
    @InjectMap(name = "result")
    private List<String> result;

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
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

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public String getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(String appraisal) {
        this.appraisal = appraisal;
    }
}

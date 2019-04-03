package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 高考选科档案子项
 */
public class SelectCourseRecordItem extends BaseRecordItem {

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

    //前置量表
    @InjectMap(name = "pre_dimension")
    private String pre_dimension;

    //学习效能比较特殊，需要展示量表列表
    @InjectMap(name = "dimensions")
    private List<SimpleDimensionResult> dimensions;

    //ACT方式的26大职业分类
    @InjectMap(name = "act_areas")
    private List<OccupationCategory> act_areas;

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public List<SimpleDimensionResult> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<SimpleDimensionResult> dimensions) {
        this.dimensions = dimensions;
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

    public String getPre_dimension() {
        return pre_dimension;
    }

    public void setPre_dimension(String pre_dimension) {
        this.pre_dimension = pre_dimension;
    }

    public List<OccupationCategory> getAct_areas() {
        return act_areas;
    }

    public void setAct_areas(List<OccupationCategory> act_areas) {
        this.act_areas = act_areas;
    }

}

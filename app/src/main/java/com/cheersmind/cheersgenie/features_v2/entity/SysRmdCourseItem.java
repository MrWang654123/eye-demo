package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 系统推荐选科子项
 */
public class SysRmdCourseItem extends BaseRecordItem {

    //话题ID
    @InjectMap(name = "topic_id")
    private String topic_id;

    //量表ID
    @InjectMap(name = "dimension_id")
    private String dimension_id;

    //量表名称
    @InjectMap(name = "dimension_name")
    private String dimension_name;

    //推荐选科
    @InjectMap(name = "subjects")
    private List<String> subjects;

    //推荐职业
    @InjectMap(name = "occupations")
    private List<String> occupations;

    //工作价值观测评结果无效时，用这个字段。
    @InjectMap(name = "result")
    private String result;

    //评价
    @InjectMap(name = "appraisal")
    private String appraisal;

    //是否完成
    @InjectMap(name = "finish")
    private boolean finish;

    //前置量表，多个用逗号隔开
    @InjectMap(name = "pre_dimenison")
    private String pre_dimenison;

    //学习效能比较特殊，需要展示量表列表
    @InjectMap(name = "dimensions")
    private List<SimpleDimensionResult> dimensions;

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

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<String> getOccupations() {
        return occupations;
    }

    public void setOccupations(List<String> occupations) {
        this.occupations = occupations;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(String appraisal) {
        this.appraisal = appraisal;
    }

    @Override
    public boolean isFinish() {
        return finish;
    }

    @Override
    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public String getPre_dimenison() {
        return pre_dimenison;
    }

    public void setPre_dimenison(String pre_dimenison) {
        this.pre_dimenison = pre_dimenison;
    }

    public List<SimpleDimensionResult> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<SimpleDimensionResult> dimensions) {
        this.dimensions = dimensions;
    }
}
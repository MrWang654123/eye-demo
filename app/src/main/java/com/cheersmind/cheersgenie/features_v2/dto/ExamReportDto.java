package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.constant.Dictionary;

import java.io.Serializable;

/**
 * 测评报告dto
 */
public class ExamReportDto implements Serializable {

    //孩子测评ID
    private String childExamId;

    //维度ID（话题或者量表）
    private String relationId;

    //维度类型（topic_dimension - 话题下的主题，topic - 话题 ，必填 ）
    private String relationType;

    //对比样本ID( 0-全国，1- 八大区，2-省，3-市，4-区)
    private int compareId = Dictionary.REPORT_COMPARE_AREA_COUNTRY;

    //维度名称（话题或者量表名称）
    private String relationName;

    public String getChildExamId() {
        return childExamId;
    }

    public void setChildExamId(String childExamId) {
        this.childExamId = childExamId;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public int getCompareId() {
        return compareId;
    }

    public void setCompareId(int compareId) {
        this.compareId = compareId;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }
}

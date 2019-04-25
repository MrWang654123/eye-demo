package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 发展档案子项
 */
public class DevelopmentRecordItem implements Serializable, MultiItemEntity {

    @InjectMap(name = "dimension_id")
    private String dimension_id;

    @InjectMap(name = "dimension_name")
    private String dimension_name;

    @InjectMap(name = "score")
    private Double score;

    @InjectMap(name = "result")
    private String result;

    @InjectMap(name = "rank")
    private Double rank;

    @InjectMap(name = "topic_dimension_id")
    private String topic_dimension_id;

    @InjectMap(name = "child_exam_id")
    private String child_exam_id;

    @InjectMap(name = "topic_id")
    private String topic_id;

    @InjectMap(name = "finish")
    private boolean finish;

    //索引位置
    private int index;

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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Double getRank() {
        return rank;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    public String getTopic_dimension_id() {
        return topic_dimension_id;
    }

    public void setTopic_dimension_id(String topic_dimension_id) {
        this.topic_dimension_id = topic_dimension_id;
    }

    public String getChild_exam_id() {
        return child_exam_id;
    }

    public void setChild_exam_id(String child_exam_id) {
        this.child_exam_id = child_exam_id;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}

package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/21.
 */

public class DimensionInfoEntity implements Serializable{

//    "unlock_flowers": 0,
//            "definition": "与父母的关系导向，是指个人在成长过程中在多大程度上仍将自己与父母的关系纳入到自己的自我概念之中。由于环境因素（例如父母的教养行为）以及生物因素（例如个人的气质特点）的差异，每个人将与父母的关系纳入到自我概念和自我认识中的程度存在不同。",
//            "dimension_name": "你与父母的关系导向",
//            "description": null,
//            "dimension_id": "c07e68da-bafd-42f1-afc8-4a266e4c287e",
//            "orderby": 1,
//            "topic_id": "f0b5d098-38bd-4d4e-bfbc-15fe342cb267",
//            "algorithm": null
    @InjectMap(name = "dimension_id")
    private String dimensionId;

    @InjectMap(name = "topic_id")
    private String topicId;

    @InjectMap(name = "dimension_name")
    private String dimensionName;

    @InjectMap(name = "icon")
    private String icon;

    @InjectMap(name = "description")
    private String description;

    @InjectMap(name = "orderby")
    private int orderby;

    @InjectMap(name = "unlock_flowers")
    private int unlockFlowers;

    @InjectMap(name = "definition")
    private String definition;

    @InjectMap(name = "algorithm")
    private String algorithm;

    @InjectMap(name = "factor_count")
    private int factorCount;

    @InjectMap(name = "flowers")
    private int flowers;

    @InjectMap(name = "use_count")
    private int useCount;

    @InjectMap(name = "child_dimension")
    private DimensionInfoChildEntity childDimension;

    @InjectMap(name = "update_time")
    private String updateTime;

    @InjectMap(name = "is_locked")
    private int isLocked;

    @InjectMap(name = "topic_dimension_id")
    private String topicDimensionId;

    public String getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrderby() {
        return orderby;
    }

    public void setOrderby(int orderby) {
        this.orderby = orderby;
    }

    public int getUnlockFlowers() {
        return unlockFlowers;
    }

    public void setUnlockFlowers(int unlockFlowers) {
        this.unlockFlowers = unlockFlowers;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getFactorCount() {
        return factorCount;
    }

    public void setFactorCount(int factorCount) {
        this.factorCount = factorCount;
    }

    public DimensionInfoChildEntity getChildDimension() {
        return childDimension;
    }

    public void setChildDimension(DimensionInfoChildEntity childDimension) {
        this.childDimension = childDimension;
    }

    public int getFlowers() {
        return flowers;
    }

    public void setFlowers(int flowers) {
        this.flowers = flowers;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(int isLocked) {
        this.isLocked = isLocked;
    }

    public String getTopicDimensionId() {
        return topicDimensionId;
    }

    public void setTopicDimensionId(String topicDimensionId) {
        this.topicDimensionId = topicDimensionId;
    }
}

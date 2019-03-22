package com.cheersmind.cheersgenie.main.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionBaseRecyclerAdapter;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 量表信息
 */
public class DimensionInfoEntity implements MultiItemEntity, Serializable {

    @InjectMap(name = "topic_dimension_id")
    private String topicDimensionId;

    @InjectMap(name = "dimension_id")
    private String dimensionId;

    @InjectMap(name = "topic_id")
    private String topicId;

    @InjectMap(name = "dimension_name")
    private String dimensionName;

    @InjectMap(name = "algorithm")
    private String algorithm;

    @InjectMap(name = "orderby")
    private int orderby;

    @InjectMap(name = "factor_count")
    private int factorCount;

    @InjectMap(name = "definition")
    private String definition;

    @InjectMap(name = "description")
    private String description;

    @InjectMap(name = "icon")
    private String icon;

    @InjectMap(name = "unlock_flowers")
    private int unlockFlowers;

    @InjectMap(name = "use_count")
    private int useCount;

    @InjectMap(name = "flowers")
    private int flowers;

    //使用对象：1、学生 2、家长
    @InjectMap(name = "suitable_user")
    private int suitableUser;

    @InjectMap(name = "appraisal_type")
    private int appraisalType;

    @InjectMap(name = "pre_dimensions")
    private String preDimensions;

    @InjectMap(name = "is_locked")
    private int isLocked;

    @InjectMap(name = "update_time")
    private String updateTime;

    @InjectMap(name = "child_dimension")
    private DimensionInfoChildEntity childDimension;

    @InjectMap(name = "question_count")
    private int questionCount;

    //预计耗时
    @InjectMap(name = "estimated_time")
    private int estimatedTime;

    //背景图
    @InjectMap(name = "background_image")
    private String backgroundImage;

    //对应的状态栏颜色
    @InjectMap(name = "background_color")
    private String backgroundColor;

    //测评ID（文章详细中的量表对象和任务子项中有返回该字段）
    @InjectMap(name = "exam_id")
    private String examId;

    //孩子测评ID（目前运用于任务子项）
    @InjectMap(name = "child_exam_id")
    private String child_exam_id;

    //是否是复制的（目前运用于任务子项）
    @InjectMap(name = "is_duplicated")
    private boolean is_duplicated;

    //是否是动态的（目前运用于任务子项）
    @InjectMap(name = "is_dynamic")
    private boolean is_dynamic;

    //是否是兄弟中的最后一个量表
    private boolean isLastInTopic;

    //是否是兄弟中的第一个量表
    private boolean isFirstInTopic;

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

    public String getPreDimensions() {
        return preDimensions;
    }

    public void setPreDimensions(String preDimensions) {
        this.preDimensions = preDimensions;
    }

    public int getSuitableUser() {
        return suitableUser;
    }

    public void setSuitableUser(int suitableUser) {
        this.suitableUser = suitableUser;
    }

    public int getAppraisalType() {
        return appraisalType;
    }

    public void setAppraisalType(int appraisalType) {
        this.appraisalType = appraisalType;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    private int itemType = ExamDimensionBaseRecyclerAdapter.LAYOUT_TYPE_DIMENSION;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isLastInTopic() {
        return isLastInTopic;
    }

    public void setLastInTopic(boolean lastInTopic) {
        isLastInTopic = lastInTopic;
    }

    public String getChild_exam_id() {
        return child_exam_id;
    }

    public void setChild_exam_id(String child_exam_id) {
        this.child_exam_id = child_exam_id;
    }

    public boolean isIs_duplicated() {
        return is_duplicated;
    }

    public void setIs_duplicated(boolean is_duplicated) {
        this.is_duplicated = is_duplicated;
    }

    public boolean isIs_dynamic() {
        return is_dynamic;
    }

    public void setIs_dynamic(boolean is_dynamic) {
        this.is_dynamic = is_dynamic;
    }

    public boolean isFirstInTopic() {
        return isFirstInTopic;
    }

    public void setFirstInTopic(boolean firstInTopic) {
        isFirstInTopic = firstInTopic;
    }
}

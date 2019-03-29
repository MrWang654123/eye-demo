package com.cheersmind.cheersgenie.features_v2.dto;

import com.cheersmind.cheersgenie.features.dto.BaseDto;

/**
 * 推荐专业dto
 */
public class RecommendMajorDto extends BaseDto {

    //孩子ID
    private String childId;

    //孩子测评ID
    private String childExamId;

    //可选，多个类型用逗号分隔如：1,2 type说明：1 高考学科推荐 2 MBTI性格测试 3 职业兴趣 4 工作价值观,
    private String fromTypes;

    public RecommendMajorDto(int page, int size) {
        super(page, size);
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildExamId() {
        return childExamId;
    }

    public void setChildExamId(String childExamId) {
        this.childExamId = childExamId;
    }

    public String getFromTypes() {
        return fromTypes;
    }

    public void setFromTypes(String fromTypes) {
        this.fromTypes = fromTypes;
    }
}

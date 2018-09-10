package com.cheersmind.cheersgenie.features.event;

import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;


/**
 * 问题提交成功事件
 */
public class QuestionSubmitSuccessEvent {

    //孩子分量表
    private DimensionInfoEntity dimension;

    public QuestionSubmitSuccessEvent(DimensionInfoEntity dimension) {
        this.dimension = dimension;
    }

    public DimensionInfoEntity getDimension() {
        return dimension;
    }

    public void setDimension(DimensionInfoEntity dimension) {
        this.dimension = dimension;
    }

}

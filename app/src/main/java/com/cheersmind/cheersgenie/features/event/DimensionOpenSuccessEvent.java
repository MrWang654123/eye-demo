package com.cheersmind.cheersgenie.features.event;

import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;


/**
 * 量表开启成功事件
 */
public class DimensionOpenSuccessEvent {

    //分量表
    private DimensionInfoEntity dimension;

    public DimensionOpenSuccessEvent(DimensionInfoEntity dimension) {
        this.dimension = dimension;
    }

    public DimensionInfoEntity getDimension() {
        return dimension;
    }

    public void setDimension(DimensionInfoEntity dimension) {
        this.dimension = dimension;
    }

}

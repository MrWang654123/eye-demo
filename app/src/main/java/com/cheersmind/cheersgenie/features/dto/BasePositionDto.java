package com.cheersmind.cheersgenie.features.dto;

import java.io.Serializable;

/**
 * 通用索引dto
 */
public class BasePositionDto implements Serializable {

    //起始位置
    private Integer beginPos;

    //页长度
    private int size;

    public BasePositionDto() {
        super();
    }

    public BasePositionDto(int beginPos, int size) {
        this.beginPos = beginPos;
        this.size = size;
    }

    public Integer getBeginPos() {
        return beginPos;
    }

    public void setBeginPos(Integer beginPos) {
        this.beginPos = beginPos;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

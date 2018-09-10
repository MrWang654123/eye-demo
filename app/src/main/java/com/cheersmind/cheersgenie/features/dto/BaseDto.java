package com.cheersmind.cheersgenie.features.dto;

import java.io.Serializable;

/**
 * 通用数据dto
 */
public class BaseDto implements Serializable {

    //页索引
    private Integer page;

    //页长度
    private int size;

    public BaseDto() {
        super();
    }

    public BaseDto(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

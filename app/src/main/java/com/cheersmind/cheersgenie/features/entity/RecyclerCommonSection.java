package com.cheersmind.cheersgenie.features.entity;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * recyclerview分组布局通用数据模型
 */
public class RecyclerCommonSection<T> extends SectionEntity  {

    //是否有查看更多的操作
    private boolean isMore;
    //其他信息
    private Object info;

    public RecyclerCommonSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public RecyclerCommonSection(boolean isHeader, String header, Object info) {
        super(isHeader, header);
        this.info = info;
    }

    public RecyclerCommonSection(boolean isHeader, String header, boolean isMore) {
        super(isHeader, header);
        this.isMore = isMore;
    }

    public RecyclerCommonSection(T t) {
        super(t);
    }

    public RecyclerCommonSection(T t, Object info) {
        super(t);
        this.info = info;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

}

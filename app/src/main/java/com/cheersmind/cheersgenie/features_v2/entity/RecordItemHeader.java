package com.cheersmind.cheersgenie.features_v2.entity;

/**
 * 档案子项的header
 */
public class RecordItemHeader extends BaseRecordItem {

    public RecordItemHeader(String title) {
        this.title = title;
    }

    private String title;

    //是否能够伸缩
    private boolean canExpand;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCanExpand() {
        return canExpand;
    }

    public RecordItemHeader setCanExpand(boolean canExpand) {
        this.canExpand = canExpand;
        return this;
    }
}

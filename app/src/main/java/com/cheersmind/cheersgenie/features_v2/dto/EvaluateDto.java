package com.cheersmind.cheersgenie.features_v2.dto;

/**
 * 评价dto
 */
public class EvaluateDto {

    //评价实体ID ：量表报告（话题量表ID）
    private String refId;

    //评价类型， 0 量表报告评价
    private int type;

    //评价选项ID
    private Integer itemId;

    //孩子ID
    private String childId;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

}

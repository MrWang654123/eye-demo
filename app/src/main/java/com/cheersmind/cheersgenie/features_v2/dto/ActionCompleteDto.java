package com.cheersmind.cheersgenie.features_v2.dto;

/**
 * 动作完成dto
 */
public class ActionCompleteDto {

    public ActionCompleteDto() {
    }

    public ActionCompleteDto(String itemId, int itemType, String childId, String childTaskItemId) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.childId = childId;
        this.childTaskItemId = childTaskItemId;
    }

    //文章id，场景id，量表id等等
    private String itemId;

    //1场景，2量表，3文章，4视频，5音频，6实践，7确认选课结果，21高考3+6选3，22高考3+7选3，23高考3+2选1+4选2
    private int itemType;

    //孩子id
    private String childId;

    //可选，不传代表不是通过具体任务的方式进入的
    private String childTaskItemId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getChildTaskItemId() {
        return childTaskItemId;
    }

    public void setChildTaskItemId(String childTaskItemId) {
        this.childTaskItemId = childTaskItemId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}

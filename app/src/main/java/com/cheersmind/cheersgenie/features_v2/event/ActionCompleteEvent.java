package com.cheersmind.cheersgenie.features_v2.event;

/**
 * 动作完成事件
 */
public class ActionCompleteEvent {

    //项Id
    private String itemId;

    public ActionCompleteEvent(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}

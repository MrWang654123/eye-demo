package com.cheersmind.cheersgenie.features_v2.event;

/**
 * 添加观察专业成功的通知事件
 */
public class AddObserveMajorSuccessEvent {

    public AddObserveMajorSuccessEvent(int count) {
        this.count = count;
    }

    //成功添加的数量
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

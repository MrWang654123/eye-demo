package com.cheersmind.cheersgenie.features_v2.event;

/**
 * 添加观察专业事件
 */
public class AddObserveMajorEvent {

    public AddObserveMajorEvent(int count) {
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

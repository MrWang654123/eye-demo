package com.cheersmind.cheersgenie.features.event;

/**
 * 在测评列表中定位到指定测评的事件
 */
public class LocationExamInListEvent {

    //测评ID
    private String examId;

    public LocationExamInListEvent(String examId) {
        this.examId = examId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }
}

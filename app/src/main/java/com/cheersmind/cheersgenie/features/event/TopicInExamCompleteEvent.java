package com.cheersmind.cheersgenie.features.event;

/**
 * 测评下的某个话题完成事件
 */
public class TopicInExamCompleteEvent {

    //测评Id
    private String examId;

    public TopicInExamCompleteEvent(String examId) {
        this.examId = examId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }
}

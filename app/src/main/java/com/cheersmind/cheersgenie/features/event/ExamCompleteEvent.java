package com.cheersmind.cheersgenie.features.event;

/**
 * 测评完成事件
 */
public class ExamCompleteEvent {

    //测评Id
    private String examId;

    public ExamCompleteEvent(String examId) {
        this.examId = examId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }
}

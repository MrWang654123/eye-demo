package com.cheersmind.cheersgenie.features_v2.event;

import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskStatus;


/**
 * 任务状态事件
 */
public class TaskStatusEvent {

    //任务状态
    private ExamTaskStatus taskStatus;

    public TaskStatusEvent(ExamTaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public ExamTaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(ExamTaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}

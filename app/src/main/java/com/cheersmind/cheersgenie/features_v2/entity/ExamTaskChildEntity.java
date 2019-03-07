package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 孩子测评任务
 */
public class ExamTaskChildEntity implements Serializable {

    //孩子任务id
    @InjectMap(name = "child_task_id")
    private String child_task_id;

    ////状态0未完成，1已完成，2已结束未完成
    @InjectMap(name = "status")
    private int status;

    public String getChild_task_id() {
        return child_task_id;
    }

    public void setChild_task_id(String child_task_id) {
        this.child_task_id = child_task_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

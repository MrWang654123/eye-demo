package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 孩子测评任务项
 */
public class TaskDetailItemChildEntity implements Serializable {

    //孩子子项ID
    @InjectMap(name = "child_item_id")
    private String child_item_id;

    //孩子测评ID
    @InjectMap(name = "child_exam_id")
    private String child_exam_id;

    //状态：0未完成，1已完成，2已结束未完成
    @InjectMap(name = "status")
    private int status;

    public String getChild_item_id() {
        return child_item_id;
    }

    public void setChild_item_id(String child_item_id) {
        this.child_item_id = child_item_id;
    }

    public String getChild_exam_id() {
        return child_exam_id;
    }

    public void setChild_exam_id(String child_exam_id) {
        this.child_exam_id = child_exam_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 孩子测评模块
 */
public class ExamModuleChildEntity implements Serializable {

    //孩子模块id
    @InjectMap(name = "child_module_id")
    private String child_module_id;

    //孩子测评id
    @InjectMap(name = "child_exam_id")
    private String child_exam_id;

    //状态：0未完成 1已完成
    @InjectMap(name = "status")
    private int status;

    public String getChild_module_id() {
        return child_module_id;
    }

    public void setChild_module_id(String child_module_id) {
        this.child_module_id = child_module_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getChild_exam_id() {
        return child_exam_id;
    }

    public void setChild_exam_id(String child_exam_id) {
        this.child_exam_id = child_exam_id;
    }
}

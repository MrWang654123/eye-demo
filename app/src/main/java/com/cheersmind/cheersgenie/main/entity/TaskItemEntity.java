package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * 任务项实体
 */
public class TaskItemEntity extends DataSupport implements Serializable {

    //测评ID
    @InjectMap(name = "exam_id")
    private String id;

    //测评名称
    @InjectMap(name = "exam_name")
    private String exam_name;

    //开始时间
    @InjectMap(name = "start_time")
    private String start_time;

    //结束时间
    @InjectMap(name = "end_time")
    private String end_time;

    //状态
    @InjectMap(name = "status")
    private int status;

    //描述
    @InjectMap(name = "description")
    private String description;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExam_name() {
        return exam_name;
    }

    public void setExam_name(String exam_name) {
        this.exam_name = exam_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 测评模块
 */
public class ExamModuleEntity implements Serializable {

    @InjectMap(name = "module_id")
    private String module_id;

    //图标
    @InjectMap(name = "module_icon")
    private String module_icon;

    //名称
    @InjectMap(name = "module_name")
    private String module_name;

    //类型：1发展测评 2生涯规划
    @InjectMap(name = "type")
    private int type;

    //描述
    @InjectMap(name = "description")
    private String description;

    //开始时间
    @InjectMap(name = "start_time")
    private String start_time;

    //结束时间
    @InjectMap(name = "end_time")
    private String end_time;

    @InjectMap(name = "child_module")
    private ExamModuleChildEntity childModule;

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getModule_icon() {
        return module_icon;
    }

    public void setModule_icon(String module_icon) {
        this.module_icon = module_icon;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ExamModuleChildEntity getChildModule() {
        return childModule;
    }

    public void setChildModule(ExamModuleChildEntity childModule) {
        this.childModule = childModule;
    }
}

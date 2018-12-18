package com.cheersmind.cheersgenie.main.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features.adapter.HistorySeminarRecyclerAdapter;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * 专题
 */
public class SeminarEntity extends AbstractExpandableItem<ExamEntity> implements Serializable, MultiItemEntity {

    @InjectMap(name = "seminar_id")
    private String id;

    //标题
    @InjectMap(name = "seminar_name")
    private String seminar_name;

    //描述
    @InjectMap(name = "description")
    private String description;

    //开始时间
    @InjectMap(name = "start_time")
    private String start_time;

    //结束时间
    @InjectMap(name = "end_time")
    private String end_time;

    //任务项集合
    @InjectMap(name = "items")
    private List<ExamEntity> exams;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeminar_name() {
        return seminar_name;
    }

    public void setSeminar_name(String seminar_name) {
        this.seminar_name = seminar_name;
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

    public List<ExamEntity> getExams() {
        return exams;
    }

    public void setExams(List<ExamEntity> exams) {
        this.exams = exams;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return HistorySeminarRecyclerAdapter.LAYOUT_TYPE_HEADER;
    }
}


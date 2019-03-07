package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.features.entity.ArticleCategory;
import com.cheersmind.cheersgenie.features.entity.ArticleTag;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 测评任务
 */
public class ExamTaskEntity implements Serializable {

    @InjectMap(name = "task_id")
    private String task_id;

    //测评名称
    @InjectMap(name = "task_name")
    private String task_name;

    //任务类型：0系统任务，1普通任务
    @InjectMap(name = "type")
    private int type;

    //图标
    @InjectMap(name = "task_icon")
    private String task_icon;

    //是否必选：0非必选，1必选
    @InjectMap(name = "required")
    private int required;

    //描述
    @InjectMap(name = "description")
    private String description;

    //关联测评数量
    @InjectMap(name = "exam_num")
    private int exam_num;

    //关联文章数量
    @InjectMap(name = "article_num")
    private int article_num;

    //关联视频数量
    @InjectMap(name = "video_num")
    private int video_num;

    //孩子任务
    @InjectMap(name = "child_task")
    private ExamTaskChildEntity childTask;

    //是否选中
    private boolean isSelected;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTask_icon() {
        return task_icon;
    }

    public void setTask_icon(String task_icon) {
        this.task_icon = task_icon;
    }

    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExam_num() {
        return exam_num;
    }

    public void setExam_num(int exam_num) {
        this.exam_num = exam_num;
    }

    public int getArticle_num() {
        return article_num;
    }

    public void setArticle_num(int article_num) {
        this.article_num = article_num;
    }

    public int getVideo_num() {
        return video_num;
    }

    public void setVideo_num(int video_num) {
        this.video_num = video_num;
    }

    public ExamTaskChildEntity getChildTask() {
        return childTask;
    }

    public void setChildTask(ExamTaskChildEntity childTask) {
        this.childTask = childTask;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

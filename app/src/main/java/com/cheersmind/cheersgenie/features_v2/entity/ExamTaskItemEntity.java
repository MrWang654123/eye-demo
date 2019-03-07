package com.cheersmind.cheersgenie.features_v2.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 测评任务内容项
 */
public class ExamTaskItemEntity extends AbstractExpandableItem<DimensionInfoEntity> implements Serializable, MultiItemEntity {

    //测评
    public static final int TYPE_EXAM = 1;
    //量表
    public static final int TYPE_DIMENSION = 2;
    //文章（目前视频、音频都归属于文章）
    public static final int TYPE_ARTICLE = 3;

    //显示项类型
    private int itemType = TYPE_ARTICLE;

    //任务ID
    @InjectMap(name = "task_id")
    private String task_id;

    //项ID
    @InjectMap(name = "item_id")
    private String item_id;

    //项名称
    @InjectMap(name = "item_name")
    private String item_name;

    //1场景，2量表，3文章，4视频，5音频，6实践，7确认选课结果，21高考3+6选3，22高考3+7选3，23高考3+2选1+4选2 --目前暂定以场景为单位，
    @InjectMap(name = "item_type")
    private int item_type;

    //参与人数
    @InjectMap(name = "use_count")
    private int use_count;

    //时长， 可能有，用类型判断
    @InjectMap(name = "time_long")
    private int time_long;

    //0不锁定 1锁定
    @InjectMap(name = "is_lock")
    private int is_lock;

    //孩子子项
    @InjectMap(name = "child_item")
    private TaskDetailItemChildEntity childItem;


    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getItem_type() {
        return item_type;
    }

    public void setItem_type(int item_type) {
        this.item_type = item_type;
    }

    public int getUse_count() {
        return use_count;
    }

    public void setUse_count(int use_count) {
        this.use_count = use_count;
    }

    public int getTime_long() {
        return time_long;
    }

    public void setTime_long(int time_long) {
        this.time_long = time_long;
    }

    public int getIs_lock() {
        return is_lock;
    }

    public void setIs_lock(int is_lock) {
        this.is_lock = is_lock;
    }

    public TaskDetailItemChildEntity getChildItem() {
        return childItem;
    }

    public void setChildItem(TaskDetailItemChildEntity childItem) {
        this.childItem = childItem;
    }

    @Override
    public int getItemType() {

        if (item_type == 1) {
            itemType = TYPE_EXAM;
        } else {
            itemType = TYPE_ARTICLE;
        }

        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getLevel() {
        return 0;
    }

}

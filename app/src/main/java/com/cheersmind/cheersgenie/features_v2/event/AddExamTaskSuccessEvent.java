package com.cheersmind.cheersgenie.features_v2.event;


import android.support.v4.util.ArrayMap;

import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;


/**
 * 添加测评任务成功的事件
 */
public class AddExamTaskSuccessEvent {

    private ArrayMap<String, ExamTaskEntity> item;

    public AddExamTaskSuccessEvent(ArrayMap<String, ExamTaskEntity> item) {
        this.item = item;
    }

    public ArrayMap<String, ExamTaskEntity> getItem() {
        return item;
    }

    public void setItem(ArrayMap<String, ExamTaskEntity> item) {
        this.item = item;
    }
}

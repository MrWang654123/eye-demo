package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.util.List;

/**
 * 任务奖励响应的根对象
 */
public class TaskRewardRoot {

    @InjectMap(name = "task_response")
    private List<TaskReward> items;

    public List<TaskReward> getItems() {
        return items;
    }

    public void setItems(List<TaskReward> items) {
        this.items = items;
    }
}

package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 任务奖励
 */
public class TaskReward implements Serializable {

    @InjectMap(name = "message")
    private String message;

    //0没有奖励 1获得积分 2获得勋章
    @InjectMap(name = "reward_type")
    private int reward_type;

    //id
    @InjectMap(name = "reward_id")
    private String reward_id;

    //奖励值？
    @InjectMap(name = "reward_value")
    private String reward_value;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getReward_type() {
        return reward_type;
    }

    public void setReward_type(int reward_type) {
        this.reward_type = reward_type;
    }

    public String getReward_id() {
        return reward_id;
    }

    public void setReward_id(String reward_id) {
        this.reward_id = reward_id;
    }

    public String getReward_value() {
        return reward_value;
    }

    public void setReward_value(String reward_value) {
        this.reward_value = reward_value;
    }
}

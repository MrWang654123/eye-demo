package com.cheersmind.cheersgenie.features_v2.event;

import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;

import java.util.List;

/**
 * 将要添加观察专业的通知事件
 */
public class AddObserveMajorEvent {

    public AddObserveMajorEvent(List<RecommendMajor> selectMajor) {
        this.selectMajor = selectMajor;
    }

    //成功添加的数量
    private List<RecommendMajor> selectMajor;

    public List<RecommendMajor> getSelectMajor() {
        return selectMajor;
    }

    public void setSelectMajor(List<RecommendMajor> selectMajor) {
        this.selectMajor = selectMajor;
    }
}

package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 积分
 */
public class IntegralEntity {

    //积分名称
    @InjectMap(name = "integral_name")
    private String integralName;

    //变更的积分分数
    @InjectMap(name = "integral_score")
    private int integralScore;

    //创建时间
    @InjectMap(name = "create_time")
    private String createTime;


    public String getIntegralName() {
        return integralName;
    }

    public void setIntegralName(String integralName) {
        this.integralName = integralName;
    }

    public int getIntegralScore() {
        return integralScore;
    }

    public void setIntegralScore(int integralScore) {
        this.integralScore = integralScore;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

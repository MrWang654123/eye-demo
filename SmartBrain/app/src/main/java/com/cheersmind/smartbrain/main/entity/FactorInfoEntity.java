package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/21.
 */

public class FactorInfoEntity implements Serializable{
//    factor_id	String	ID，UUID
//    dimension_id	String	所属分量表ID，UUID
//    factor_name	String	名称
//    cost_time	int	倒计时时间
//    flowers	int	每题的鲜花
//    icon	String	图片url
//    deccription	String	介绍
//    orderby	Int	排序

    @InjectMap(name = "factor_id")
    private String factorId;

    @InjectMap(name = "dimension_id")
    private String dimensionId;

    @InjectMap(name = "factor_name")
    private String factorName;

    @InjectMap(name = "cost_time")
    private int costTime;

    @InjectMap(name = "flowers")
    private int flowers;

    @InjectMap(name = "icon")
    private String icon;

    @InjectMap(name = "deccription")
    private String deccription;

    @InjectMap(name = "orderby")
    private int orderby;

    @InjectMap(name = "question_count")
    private int questionCount;

    @InjectMap(name = "child_factor")
    private FactorInfoChildEntity childFactor;

    @InjectMap(name = "instruction")
    private String instruction;

    private int stage;//当前因子的阶段数，按列表排列

    public String getFactorId() {
        return factorId;
    }

    public void setFactorId(String factorId) {
        this.factorId = factorId;
    }

    public String getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
    }

    public String getFactorName() {
        return factorName;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public int getFlowers() {
        return flowers;
    }

    public void setFlowers(int flowers) {
        this.flowers = flowers;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDeccription() {
        return deccription;
    }

    public void setDeccription(String deccription) {
        this.deccription = deccription;
    }

    public int getOrderby() {
        return orderby;
    }

    public void setOrderby(int orderby) {
        this.orderby = orderby;
    }

    public FactorInfoChildEntity getChildFactor() {
        return childFactor;
    }

    public void setChildFactor(FactorInfoChildEntity childFactor) {
        this.childFactor = childFactor;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}

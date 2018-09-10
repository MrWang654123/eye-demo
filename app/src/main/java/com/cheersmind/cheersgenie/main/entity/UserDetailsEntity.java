package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/11.
 */

public class UserDetailsEntity implements Serializable{

    @InjectMap(name = "user_id")
    private long userId;

    @InjectMap(name = "user_name")
    private String userName;

    @InjectMap(name = "sex")
    private int sex;

    @InjectMap(name = "age")
    private int age;

    @InjectMap(name = "flowers")
    private int flowers;

    @InjectMap(name = "org_id")
    private long orgId;

    @InjectMap(name = "create_time")
    private String createTime;

    @InjectMap(name = "update_time")
    private String updateTime;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getFlowers() {
        return flowers;
    }

    public void setFlowers(int flowers) {
        this.flowers = flowers;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}

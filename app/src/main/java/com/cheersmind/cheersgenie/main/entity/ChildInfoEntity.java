package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/21.
 */

public class ChildInfoEntity extends DataSupport implements Serializable{

//            "child_id": "99988864-64e2-41ab-4389-a407b28df394",
//                    "child_name": "苏小梅",
//                    "sex": 2,//孩子性别 1-男，2-女
//                    "user_id": 0,
//                    "class_id": "56fec92a-fd5f-7c02-828d-e30c3646e544",
//                    "birth_day": "1998-07-09T00:00:00.000+0800",
//                    "avatar": null,
//                    "class_name": "49班（珍珠班）",
//                    "school_id": "f65291ab-dcc3-11e7-8a83-f0761cdecc9e",
//                    "grade": "高三",
//                    "period": "高中",
//                    "school_name": "宁夏育才中学",
//                    "parent_role": 0 //用户的角色，1-父亲，2-母亲，3-爷爷/外公，4-奶奶/外婆  99其他

    @InjectMap(name = "child_id")
    private String childId;
    @InjectMap(name = "child_name")
    private String childName;
    @InjectMap(name = "sex")
    private int sex;
    @InjectMap(name = "usr_id")
    private String usrId;
    @InjectMap(name = "birth_day")
    private String birthDay;
    @InjectMap(name = "avatar")
    private String avatar;
    @InjectMap(name = "invite_code")
    private String inviteCode;
    @InjectMap(name = "class_id")
    private String classId;
    @InjectMap(name = "class_name")
    private String class_name;
    @InjectMap(name = "school_id")
    private String schoolId;
    @InjectMap(name = "grade")
    private String grade;
    @InjectMap(name = "period")
    private String period;
    @InjectMap(name = "school_name")
    private String schoolName;
    //用户的角色，1-父亲，2-母亲，3-爷爷/外公，4-奶奶/外婆  99其他
    @InjectMap(name = "parent_role")
    private int parentRole;

    private boolean isDefaultChild;
    private int flowerCount;

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public boolean isDefaultChild() {
        return isDefaultChild;
    }

    public void setDefaultChild(boolean defaultChild) {
        isDefaultChild = defaultChild;
    }

    public int getFlowerCount() {
        return flowerCount;
    }

    public void setFlowerCount(int flowerCount) {
        this.flowerCount = flowerCount;
    }

    public int getParentRole() {
        return parentRole;
    }

    public void setParentRole(int parentRole) {
        this.parentRole = parentRole;
    }
}

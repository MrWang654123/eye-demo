package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/21.
 */

public class ChildInfoEntity extends DataSupport implements Serializable{

//    "child_id": "1",
//            "child_name": "冯猛儿子1",
//            "sex": 1,
//            "usr_id": 0,
//            "class_id": "1",
//            "birth_day": "2017-10-03T00:00:00.000+0800",
//            "avatar": "1",
//            "invite_code": "222",
//            "class_name": "test",
//            "school_id": "1",
//            "grade": "2017",
//            "period": "4",
//            "school_name": "test"
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
    private String className;
    @InjectMap(name = "school_id")
    private String schoolId;
    @InjectMap(name = "grade")
    private String grade;
    @InjectMap(name = "period")
    private String period;
    @InjectMap(name = "school_name")
    private String schoolName;

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

    @Override
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
}

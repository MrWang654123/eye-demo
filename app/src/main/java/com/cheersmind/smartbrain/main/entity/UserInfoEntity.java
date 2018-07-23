package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/11/6.
 */

public class UserInfoEntity extends DataSupport {

//    "nick_name": "qstest002",
//            "user_name": "qstest002",
//            "email": "",
//            "nick_name_full": "qstest002",
//            "create_time": "2017-10-14T22:20:16.000+0800",
//            "isbind_mobile": 0,
//            "user_id": 2109096778,
//            "isbind_email": 0,
//            "nick_name_short": "qstest002",
//            "mobile": "",
//            "enable_status": 1

    @InjectMap(name = "region")
    private String region;

    @InjectMap(name = "nick_name")
    private String nickName;

    @InjectMap(name = "user_name")
    private String userName;

    @InjectMap(name = "email")
    private String email;

    @InjectMap(name = "nick_name_full")
    private String nickNameFull;

    @InjectMap(name = "create_time")
    private String createTime;

    @InjectMap(name = "isbind_mobile")
    private int isbindMobile;

    @InjectMap(name = "user_id")
    private String userId;

    @InjectMap(name = "isbind_email")
    private int isbindEmail;

    @InjectMap(name = "mobile")
    private String mobile;

    @InjectMap(name = "nick_name_short")
    private String nickNameShort;

    @InjectMap(name = "enable_status")
    private String enableStatus;

    @InjectMap(name = "org_exinfo")
    private OrgExinfo orgExinfo;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickNameFull() {
        return nickNameFull;
    }

    public void setNickNameFull(String nickNameFull) {
        this.nickNameFull = nickNameFull;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getIsbindMobile() {
        return isbindMobile;
    }

    public void setIsbindMobile(int isbindMobile) {
        this.isbindMobile = isbindMobile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsbindEmail() {
        return isbindEmail;
    }

    public void setIsbindEmail(int isbindEmail) {
        this.isbindEmail = isbindEmail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickNameShort() {
        return nickNameShort;
    }

    public void setNickNameShort(String nickNameShort) {
        this.nickNameShort = nickNameShort;
    }

    public String getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(String enableStatus) {
        this.enableStatus = enableStatus;
    }

    public OrgExinfo getOrgExinfo() {
        return orgExinfo;
    }

    public void setOrgExinfo(OrgExinfo orgExinfo) {
        this.orgExinfo = orgExinfo;
    }
}

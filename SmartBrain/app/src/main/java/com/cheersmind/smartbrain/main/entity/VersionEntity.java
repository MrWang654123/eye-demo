package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

/**
 * Created by Administrator on 2017/12/17.
 */

public class VersionEntity {

    @InjectMap(name = "app_history_id")
    private String appHistoryId;//版本 id

    @InjectMap(name = "app_id")
    private String appId; // app id

    @InjectMap(name = "os")
    private int os; //操作系统，1-安卓，2-iOS，默认安卓

    @InjectMap(name = "is_latest")
    private int isLatest;//是否最新版本，0否，1是，默认0

    @InjectMap(name = "version_name")
    private String versionName;

    @InjectMap(name = "version_code")
    private String versionCode;

    @InjectMap(name = "update_url")
    private String updateUrl;// 版本的更新地址，若是iOS则是appstore的页面地址，安卓则是apk包下载地址

    @InjectMap(name = "description")
    private String description;// 版本说明，需要支持HTML

    @InjectMap(name = "update_time")
    private String updateTime;

    @InjectMap(name = "force_update")
    private int forceUpdate;// 是否需要强制更新，0-不需要，1-需要强制更新

    public String getAppHistoryId() {
        return appHistoryId;
    }

    public void setAppHistoryId(String appHistoryId) {
        this.appHistoryId = appHistoryId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }

    public int getIsLatest() {
        return isLatest;
    }

    public void setIsLatest(int isLatest) {
        this.isLatest = isLatest;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}

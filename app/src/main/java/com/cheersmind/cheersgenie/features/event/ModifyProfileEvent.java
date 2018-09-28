package com.cheersmind.cheersgenie.features.event;

/**
 * 修改头像事件
 */
public class ModifyProfileEvent {

    //头像url
    private String profileUrl;

    public ModifyProfileEvent(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}

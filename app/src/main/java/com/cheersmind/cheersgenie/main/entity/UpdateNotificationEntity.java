package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * Created by Administrator on 2018/7/31.
 */

public class UpdateNotificationEntity {

    @InjectMap(name = "is_notice")
    private boolean isNotice;

    @InjectMap(name = "message")
    private String message;

    public boolean isNotice() {
        return isNotice;
    }

    public void setNotice(boolean notice) {
        isNotice = notice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

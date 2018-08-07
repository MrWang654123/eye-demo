package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

/**
 * Created by Administrator on 2018/8/7.
 */

public class SystemTimeEntity {
//    {"datetime":"2018-08-07T20:25:30.207+0800","timestamp":1533644730207}
    @InjectMap(name = "datetime")
    private String datetime;

    @InjectMap(name = "timestamp")
    private String timestamp;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

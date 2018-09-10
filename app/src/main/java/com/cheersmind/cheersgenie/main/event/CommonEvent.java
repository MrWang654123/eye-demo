package com.cheersmind.cheersgenie.main.event;

/**
 * Created by Administrator on 2018/6/15.
 */

public class CommonEvent {

    public static final String EVENT_REFRESH_REPORT = "refresh_report_main";//通知首页mainactivity需要刷新报告界面
    public static final String EVENT_REFRESH_REPORT_PAGE = "refresh_report_fragment";//刷新首页报告界面

    private String eventType;

    public CommonEvent(String eventType){
        setEventType(eventType);
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}

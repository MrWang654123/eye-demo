package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

/**
 * Created by Administrator on 2018/7/30.
 */

public class ErrorCodeEntity {

    @InjectMap(name = "host_id")
    private String hostId;

    @InjectMap(name = "request_id")
    private String requestId;

    @InjectMap(name = "server_time")
    private String serverTime;

    @InjectMap(name = "code")
    private String code;

    @InjectMap(name = "message")
    private String message;

    @InjectMap(name = "detail")
    private String detail;

    @InjectMap(name = "cause")
    private String cause;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}

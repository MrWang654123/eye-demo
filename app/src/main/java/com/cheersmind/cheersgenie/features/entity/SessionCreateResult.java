package com.cheersmind.cheersgenie.features.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 会话创建结果
 */
public class SessionCreateResult {

    //会话ID
    @InjectMap(name = "session_id")
    private String sessionId;

    ////操作次数，注册次数或登录错误次数
    @InjectMap(name = "opt_num")
    private int optNum;

    //是否正常，当注册次数或登录错误次数超过阈值时，该字段返回false，客户端需要加载图片验证码
    @InjectMap(name = "normal")
    private boolean normal;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getOptNum() {
        return optNum;
    }

    public void setOptNum(int optNum) {
        this.optNum = optNum;
    }

    public boolean getNormal() {
        return normal;
    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

}

package com.cheersmind.cheersgenie.features.event;

/**
 * 最后操作的测评通知
 */
public class LastHandleExamEvent {

    //更新操作（开启新的量表之后就更新）
    public static final int HANDLE_TYPE_UPDATE = 0;

    //完成测评（刚完成一个测评，此时是没有最新操作测评的）
    public static final int HANDLE_TYPE_COMPLETE = 1;

    //处理类型
    private int handleType;

    public LastHandleExamEvent(int handleType) {
        this.handleType = handleType;
    }

    public int getHandleType() {
        return handleType;
    }

    public void setHandleType(int handleType) {
        this.handleType = handleType;
    }
}

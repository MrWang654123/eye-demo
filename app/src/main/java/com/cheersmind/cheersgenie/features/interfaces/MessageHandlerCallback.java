package com.cheersmind.cheersgenie.features.interfaces;

import android.os.Message;

/**
 * Handler接收消息的回调
 */
public interface MessageHandlerCallback {

    /**
     * 处理消息
     * @param msg
     */
    public void onHandleMessage(Message msg);

}

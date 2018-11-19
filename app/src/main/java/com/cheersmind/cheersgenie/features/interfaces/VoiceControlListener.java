package com.cheersmind.cheersgenie.features.interfaces;

/**
 * 语音播放控制
 */
public interface VoiceControlListener {

    /**
     * 播放
     */
    void play();

    /**
     * 停止
     */
    void stop();

    /**
     * 单个文本播放结束（批量的单个）
     * @param utteranceId 标识ID
     */
    void speechFinish(String utteranceId);

    /**
     * 单个文本开始播放（批量的单个）
     * @param utteranceId 标识ID
     */
    void speechStart(String utteranceId);

}

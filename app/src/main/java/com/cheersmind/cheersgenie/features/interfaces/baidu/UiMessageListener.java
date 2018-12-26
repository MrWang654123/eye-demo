package com.cheersmind.cheersgenie.features.interfaces.baidu;

import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.cheersmind.cheersgenie.BuildConfig;

/**
 * 在 MessageListener的基础上，和UI配合。
 * Created by fujiayi on 2017/9/14.
 */

public class UiMessageListener extends MessageListener {

    private Handler mainHandler;

    private static final String TAG = "UiMessageListener";

    public UiMessageListener(Handler mainHandler) {
        super();
        this.mainHandler = mainHandler;
    }

    /**
     * 合成数据和进度的回调接口，分多次回调。
     * 注意：progress表示进度，与播放到哪个字无关
     * @param utteranceId
     * @param data 合成的音频数据。该音频数据是采样率为16K，2字节精度，单声道的pcm数据。
     * @param progress 文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] data, int progress) {
        // sendMessage("onSynthesizeDataArrived");
        if (mainHandler != null) {
            mainHandler.sendMessage(mainHandler.obtainMessage(UI_CHANGE_SYNTHES_TEXT_SELECTION, progress, 0));
        }
    }

    /**
     * 播放进度回调接口，分多次回调
     * 注意：progress表示进度，与播放到哪个字无关
     *
     * @param utteranceId
     * @param progress 文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        // sendMessage("onSpeechProgressChanged");
        if (mainHandler != null) {
            mainHandler.sendMessage(mainHandler.obtainMessage(UI_CHANGE_INPUT_TEXT_SELECTION, progress, 0));
            if (BuildConfig.DEBUG) {
//                System.out.println("播放进度：" + progress);
            }
        }
    }

    protected void sendMessage(String message) {
        sendMessage(message, false);
    }

    @Override
    protected void sendMessage(String message, boolean isError) {
        sendMessage(message, isError, PRINT);
    }


    protected void sendMessage(String message, boolean isError, int action) {
        super.sendMessage(message, isError);
        if (mainHandler != null) {
            Message msg = Message.obtain();
            msg.what = action;
            msg.obj = message + "\n";
            mainHandler.sendMessage(msg);
            Log.i(TAG, message);
        }
    }


    /**
     * 开始播放
     * @param utteranceId
     */
    @Override
    public void onSpeechStart(String utteranceId) {
        super.onSpeechStart(utteranceId);

        if (mainHandler != null) {
            Message.obtain(mainHandler, SPEECH_START, utteranceId).sendToTarget();
        }
    }

    /**
     * 播放结束
     * @param utteranceId 标识ID
     */
    @Override
    public void onSpeechFinish(String utteranceId) {
        super.onSpeechFinish(utteranceId);

        if (mainHandler != null) {
            Message.obtain(mainHandler, SPEECH_FINISH, utteranceId).sendToTarget();
        }
    }


    /**
     * 合成或者播放错误
     * @param utteranceId 标识ID
     * @param speechError 包含错误码和错误信息
     */
    @Override
    public void onError(String utteranceId, SpeechError speechError) {
        super.onError(utteranceId, speechError);

        if (mainHandler != null) {
            Message.obtain(mainHandler, ERROR, utteranceId).sendToTarget();
        }
    }


}

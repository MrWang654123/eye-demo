package com.cheersmind.cheersgenie.main.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;

import com.cheersmind.cheersgenie.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * 短音频播放工具
 */
public class SoundPlayUtils {

    // SoundPool对象
    private SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_MUSIC, 5);

//    private MediaPlayer mediaPlayer;

    private static SoundPlayUtils soundPlayUtils;

    public static SoundPlayUtils getInstance() {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }
        return soundPlayUtils;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {

        // 初始化声音
        mSoundPlayer.load(context, R.raw.num_sound, 1);// 1
        mSoundPlayer.load(context, R.raw.go_sound, 1);// 2
        mSoundPlayer.load(context, R.raw.question_click, 1);// 3
        mSoundPlayer.load(context, R.raw.page_next, 1);// 4

//        mediaPlayer = MediaPlayer.create(context, R.raw.question_click);

//        //初始化声音开关
//        setSoundStatus(context, getSoundStatus(context));
    }


    /**
     * 释放资源
     */
    public void release() {
        try {
            if (mSoundPlayer != null) {
                mSoundPlayer.release();
                mSoundPlayer = null;
            }

//            if (mediaPlayer != null) {
//                mediaPlayer.release();
//                mediaPlayer = null;
//            }

            if (soundPlayUtils != null) {
                soundPlayUtils = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 播放音频
     */
    public void play() {
        try {

//            if(!getSoundStatus(context)){
//                return;
//            }

//            if (mediaPlayer != null) {
//                mediaPlayer.start();
//            }

            play(3);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音频
     */
    public void play(int soundID) {
        try {
            if (mSoundPlayer != null) {
                mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置音频可播放状态
     * @param context 上下文
     * @param isSound true：可播放
     */
    public void setSoundStatus(Context context, boolean isSound){
        SharedPreferences soundPreference = context.getSharedPreferences("setting_sound", MODE_PRIVATE);
        SharedPreferences.Editor editor = soundPreference.edit();
        editor.putBoolean("is_sound",isSound);
        editor.apply();
    }

    /**
     * 获取音频可播放状态
     * @param context 上下文
     * @return true：可播放
     */
    public boolean getSoundStatus(Context context){
        SharedPreferences soundPreference = context.getSharedPreferences("setting_sound", MODE_PRIVATE);
        return soundPreference.getBoolean("is_sound",true);//默认开启声音
    }

}

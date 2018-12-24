package com.cheersmind.cheersgenie.main.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;

import com.cheersmind.cheersgenie.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/12/11.
 */

public class SoundPlayUtils {

    // SoundPool对象
    private static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_MUSIC, 5);
    private static SoundPlayUtils soundPlayUtils;

    /**
     * 初始化
     *
     * @param context
     */
    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }

        // 初始化声音
        mSoundPlayer.load(context, R.raw.num_sound, 1);// 1
        mSoundPlayer.load(context, R.raw.go_sound, 1);// 2
        mSoundPlayer.load(context, R.raw.question_click, 1);// 3
        mSoundPlayer.load(context, R.raw.page_next, 1);// 4

        //初始化声音开关
        setSoundStatus(context, getSoundStatus(context));

        return soundPlayUtils;
    }

//    /**
//     * 释放资源
//     */
//    public static void release() {
//        try {
//            if (mSoundPlayer != null) {
//                mSoundPlayer.release();
//                mSoundPlayer = null;
//            }
//
//            if (soundPlayUtils != null) {
//                soundPlayUtils = null;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(SoundPool soundPlayer, int soundID) {
        if (soundPlayer != null) {
            try {
                soundPlayer.play(soundID, 1, 1, 0, 0, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(Context context, int soundID) {
        if(!getSoundStatus(context)){
            return;
        }
        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
    }

    public static void setSoundStatus(Context context, boolean isSound){
        SharedPreferences soundPreference = context.getSharedPreferences("setting_sound", MODE_PRIVATE);
        SharedPreferences.Editor editor = soundPreference.edit();
        editor.putBoolean("is_sound",isSound);
        editor.apply();
    }

    public static boolean getSoundStatus(Context context){
        SharedPreferences soundPreference = context.getSharedPreferences("setting_sound", MODE_PRIVATE);
        return soundPreference.getBoolean("is_sound",true);//默认开启声音
    }

}

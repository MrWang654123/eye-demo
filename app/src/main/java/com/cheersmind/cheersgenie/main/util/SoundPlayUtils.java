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
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_MUSIC, 5);
    public static SoundPlayUtils soundPlayUtils;
    // 上下文
    static Context mContext;

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
        mContext = context;

        mSoundPlayer.load(mContext, R.raw.num_sound, 1);// 1
        mSoundPlayer.load(mContext, R.raw.go_sound, 1);// 2
        mSoundPlayer.load(mContext, R.raw.question_click, 1);// 3
        mSoundPlayer.load(mContext, R.raw.page_next, 1);// 4

        //初始化声音开关
        setSoundStatus(getSoundStatus());

        return soundPlayUtils;
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(int soundID) {
        if(!getSoundStatus()){
            return;
        }
        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
    }

    public static void setSoundStatus(boolean isSound){
        SharedPreferences soundPreference = mContext.getSharedPreferences("setting_sound", MODE_PRIVATE);
        SharedPreferences.Editor editor = soundPreference.edit();
        editor.putBoolean("is_sound",isSound);
        editor.apply();
    }

    public static boolean getSoundStatus(){
        SharedPreferences soundPreference = mContext.getSharedPreferences("setting_sound", MODE_PRIVATE);
        return soundPreference.getBoolean("is_sound",true);//默认开启声音
    }

}

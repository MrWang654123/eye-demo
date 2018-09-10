package com.cheersmind.cheersgenie.features.view.video;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserAction;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * 只能全屏且横屏播放（饺子视频播放器）
 */
public class JZVideoPlayerStandardHorizontal extends JZVideoPlayerStandard {
    public JZVideoPlayerStandardHorizontal(Context context) {
        super(context);
    }

    public JZVideoPlayerStandardHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void startVideo() {
        //全屏
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            super.startVideo();
        } else {
            super.startVideo();
            //进入全屏
            onEvent(JZUserAction.ON_ENTER_FULLSCREEN);
            startWindowFullscreen();
        }

    }

    /**
     * 退出全屏模式的时候释放资源结束
     */
    @Override
    public void playOnThisJzvd() {
        super.playOnThisJzvd();
//        JZVideoPlayer.goOnPlayOnPause();//暂停后，下次不走startVideo
        JZMediaManager.instance().releaseMediaPlayer();
        JZVideoPlayerManager.completeAll();
    }

}

package com.cheersmind.cheersgenie.features.view.video;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.EncryptUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.module.login.EnvHostManager;

import java.util.Map;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserAction;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * 只能全屏且横屏播放（饺子视频播放器）
 */
public class JZVideoPlayerStandardHorizontal extends JZVideoPlayerStandard {
    //视频ID
    String videoId;
    //视频标题
    String title;

    //视频真实url
    String videoRealUrl;
    //最后加载url的时间戳
    long lastLoadUrlTimestamp = 0;
    //失效间隔30分钟
    private static final long INVALID_INTERVAL = 30 * 60 * 1000;

    public JZVideoPlayerStandardHorizontal(Context context) {
        super(context);
    }

    public JZVideoPlayerStandardHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);

        startButton.setOnClickListener(this);
        mRetryBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.start) {
            if (videoUrlCheck()) {
                doStartBtnClick();
            } else {
                if (TextUtils.isEmpty(videoId)) {
                    doStartBtnClick();
                } else {
                    onStatePreparing();
                    queryVideoRealUrl(videoId, title, R.id.start);
                }
            }

        } else if (i == R.id.retry_btn) {
            if (videoUrlCheck()) {
                doRetryBtnClick();
            } else {
                if (TextUtils.isEmpty(videoId)) {
                    doRetryBtnClick();
                } else {
                    onStatePreparing();
                    //此处用的是R.id.start
                    queryVideoRealUrl(videoId, title, R.id.start);
                }
            }

        } else {
            super.onClick(v);
        }
    }

    /**
     *  开始按钮点击响应
     */
    private void doStartBtnClick() {
        Log.i(TAG, "onClick start [" + this.hashCode() + "] ");
        if (dataSourceObjects == null || JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) == null) {
            Toast.makeText(getContext(), getResources().getString(cn.jzvd.R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentState == CURRENT_STATE_NORMAL) {
            if (!JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("file") && !
                    JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("/") &&
                    !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                showWifiDialog();
                return;
            }
            startVideo();
            onEvent(JZUserAction.ON_CLICK_START_ICON);//开始的事件应该在播放之后，此处特殊
        } else if (currentState == CURRENT_STATE_PLAYING) {
            onEvent(JZUserAction.ON_CLICK_PAUSE);
            Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
            JZMediaManager.pause();
            onStatePause();
        } else if (currentState == CURRENT_STATE_PAUSE) {
            onEvent(JZUserAction.ON_CLICK_RESUME);
            JZMediaManager.start();
            onStatePlaying();
        } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            onEvent(JZUserAction.ON_CLICK_START_AUTO_COMPLETE);
            startVideo();
        }
    }


    /**
     * 重新加载按钮响应
     */
    private void doRetryBtnClick() {
        if (dataSourceObjects == null || JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) == null) {
            Toast.makeText(getContext(), getResources().getString(cn.jzvd.R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("file") && !
                JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("/") &&
                !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
            showWifiDialog();
            return;
        }
        initTextureView();//和开始播放的代码重复
        addTextureView();
        JZMediaManager.setDataSource(dataSourceObjects);
        JZMediaManager.setCurrentDataSource(JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
        onStatePreparing();
        onEvent(JZUserAction.ON_CLICK_START_ERROR);
    }



    /**
     * 退出全屏模式的时候释放资源结束
     */
    @Override
    public void playOnThisJzvd() {
        super.playOnThisJzvd();

//        JZVideoPlayer.goOnPlayOnPause();//暂停后，下次不走startVideo
        /*
        JZMediaManager.instance().releaseMediaPlayer();
        JZVideoPlayerManager.completeAll();
        */
    }


    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 地址有效性验证
     * @return true：有效
     */
    private boolean videoUrlCheck() {
        boolean res = false;

        if (!TextUtils.isEmpty(videoRealUrl)) {
            long curTimestamp = System.currentTimeMillis();
            if (curTimestamp - lastLoadUrlTimestamp < INVALID_INTERVAL) {
                res = true;
            }
        }

        return res;
    }


    /**
     * 请求视频真实地址
     * @param videoId
     */
    private void queryVideoRealUrl(String videoId, final String title, final int btnId) {
        final long resourceTimestamp = System.currentTimeMillis();
        long curTimestamp = resourceTimestamp / 1000;
        String curTimestampStr = String.valueOf(curTimestamp);
        String key = EnvHostManager.getInstance().getVideoSignKey();
        String sign = signVideoUrl(videoId, key, curTimestampStr);
        //请求视频真实地址
        DataRequestService.getInstance().getVideoRealUrl(videoId, sign, curTimestampStr, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                try {
                    String bodyStr = e.getMessage();
                    Map map = JsonUtil.fromJson(bodyStr, Map.class);
                    ErrorCodeEntity errorCodeEntity = InjectionWrapperUtil.injectMap(map, ErrorCodeEntity.class);

                    //参数错误，若出现错误可获取错误中的时间字段server_time，并转为时间戳使用。
                    if (errorCodeEntity != null && ErrorCode.PSY_INVALID_PARAM.equals(errorCodeEntity.getCode())) {
                        videoRealUrl = null;
                    }

                } catch (Exception err) {
                    err.printStackTrace();

                } finally {
//                    if (btnId == R.id.start) {
//                        doStartBtnClick();
//
//                    } else if (btnId == cn.jzvd.R.id.retry_btn) {
//                        doRetryBtnClick();
//
//                    }
                    onStateError();
                    if (isCurrentPlay()) {
                        JZMediaManager.instance().releaseMediaPlayer();
                    }
                }

            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    String tempVideoRealUrl = (String) dataMap.get("display_url");
                    if (TextUtils.isEmpty(tempVideoRealUrl)) {
                        throw new NullPointerException();
                    }

                    videoRealUrl = tempVideoRealUrl;
                    setUp(videoRealUrl,JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, title);

                    lastLoadUrlTimestamp = resourceTimestamp;

                } catch (Exception e) {
                    e.printStackTrace();
                    videoRealUrl = null;

                } finally {
                    if (btnId == R.id.start) {
                        doStartBtnClick();

                    } else if (btnId == cn.jzvd.R.id.retry_btn) {
                        doRetryBtnClick();

                    }
                }
            }
        });
    }


    /**
     * 获取视频地址的请求签名
     * @param videoId
     * @param key
     * @param timestamp
     * @return
     */
    private String signVideoUrl(String videoId, String key, String timestamp) {
        return EncryptUtil.md5(key + videoId+ timestamp).toLowerCase();
    }

}

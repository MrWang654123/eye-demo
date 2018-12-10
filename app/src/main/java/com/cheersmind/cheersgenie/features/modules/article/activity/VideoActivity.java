package com.cheersmind.cheersgenie.features.modules.article.activity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.VideoConstant;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * 视频页面
 */
public class VideoActivity extends BaseActivity {

    private static final String ARTICLE_ID = "article_id";
    //文章Id
    String articleId;

    @BindView(R.id.jz_video)
    JZVideoPlayerStandard jzVideo;


    /**
     * 开启文章详情页面
     *
     * @param articleId
     */
    public static void startVideoActivity(Context context, String articleId) {
        Intent intent = new Intent(context, VideoActivity.class);
        Bundle extras = new Bundle();
        extras.putString(ARTICLE_ID, articleId);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_video;
    }

    @Override
    protected String settingTitle() {
        return "文章详情";
    }

    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        jzVideo.setUp(VideoConstant.videoUrlList[0]
                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "饺子不信");
        Glide.with(this)
                .load(VideoConstant.videoThumbList[0])
                .into(jzVideo.thumbImageView);

        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        if (getIntent() == null || getIntent().getExtras() == null) {
            ToastUtil.showShort(getApplication(), "数据传递有误");
            return;
        }

        articleId = getIntent().getExtras().getString(ARTICLE_ID);

        //加载文章详情
//        loadArticleDetail(articleId);

        //加载评论数据
//        articleId = "1";
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();

        //Change these two variables back
        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (jzVideo.backPress()) {
            return;
        }
        super.onBackPressed();
    }

}


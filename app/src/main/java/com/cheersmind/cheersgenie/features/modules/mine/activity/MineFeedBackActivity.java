package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;

import java.util.concurrent.Callable;

/**
 * 用户反馈
 */
public class MineFeedBackActivity extends BaseActivity {

    @Override
    protected int setContentView() {
        return R.layout.activity_mine_feed_back;
    }

    @Override
    protected String settingTitle() {
        return "用户反馈";
    }

    @Override
    protected void onInitView() {
//        FeedbackAPI.setTitleBarHeight(0);
        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
        final Fragment feedback = FeedbackAPI.getFeedbackFragment();
        // must be called
        FeedbackAPI.setFeedbackFragment(new Callable() {
            @Override
            public Object call() throws Exception {
                transaction.replace(R.id.fl_fragment, feedback);
                transaction.commit();
                return null;
            }
        }/*success callback*/, null/*fail callback*/);
    }

    @Override
    protected void onInitData() {

    }

}

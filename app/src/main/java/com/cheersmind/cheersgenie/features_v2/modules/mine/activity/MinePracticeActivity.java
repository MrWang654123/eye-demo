package com.cheersmind.cheersgenie.features_v2.modules.mine.activity;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.mine.fragment.MinePracticeFragment;

/**
 * 我的实践
 */
public class MinePracticeActivity extends BaseActivity {

    /**
     * 启动我的实践页面
     * @param context 上下文
     */
    public static void startMinePracticeActivity(Context context) {
        Intent intent = new Intent(context, MinePracticeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_white_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "我的实践";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(MinePracticeActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = MinePracticeFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //我的实践
            MinePracticeFragment fragment = new MinePracticeFragment();
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

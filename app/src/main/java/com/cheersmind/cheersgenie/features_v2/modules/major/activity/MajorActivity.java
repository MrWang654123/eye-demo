package com.cheersmind.cheersgenie.features_v2.modules.major.activity;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.major.fragment.MajorFragment;

/**
 * 专业
 */
public class MajorActivity extends BaseActivity {

    /**
     * 启动专业页面
     * @param context 上下文
     */
    public static void startMajorActivity(Context context) {
        Intent intent = new Intent(context, MajorActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_white_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "专业大全";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(MajorActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = MajorFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //专业
            MajorFragment fragment = new MajorFragment();
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

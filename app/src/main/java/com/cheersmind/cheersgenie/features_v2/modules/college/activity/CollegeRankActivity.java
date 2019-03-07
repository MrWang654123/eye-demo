package com.cheersmind.cheersgenie.features_v2.modules.college.activity;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.interfaces.BackPressedHandler;
import com.cheersmind.cheersgenie.features_v2.modules.college.fragment.CollegeRankFragment;

/**
 * 院校排名
 */
public class CollegeRankActivity extends BaseActivity implements BackPressedHandler {

    /**
     * 启动院校排名页面
     * @param context 上下文
     */
    public static void startCollegeActivity(Context context) {
        Intent intent = new Intent(context, CollegeRankActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_white_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "院校排名";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(CollegeRankActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    public void onBackPressed() {
        if (!hasHandlerBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onInitData() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = CollegeRankFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //院校排名
            CollegeRankFragment fragment = new CollegeRankFragment();
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

    /**
     * 是否处理了回退
     * @return true：回退被处理了
     */
    @Override
    public boolean hasHandlerBackPressed() {
        boolean res = false;
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = CollegeRankFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //根据fragment的处理结果赋值
        if (fragmentByTag != null && fragmentByTag instanceof BackPressedHandler) {
            res = ((BackPressedHandler) fragmentByTag).hasHandlerBackPressed();
        }
        return res;
    }

}

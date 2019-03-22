package com.cheersmind.cheersgenie.features_v2.modules.occupation.activity;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.interfaces.BackPressedHandler;
import com.cheersmind.cheersgenie.features_v2.modules.college.fragment.CollegeRankFragment;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.fragment.OccupationFragment;
import com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment.TrackRecordFragment;

/**
 * 职业
 */
public class OccupationActivity extends BaseActivity implements BackPressedHandler {

    /**
     * * 启动职业页面
     * @param context 上下文
     */
    public static void startOccupationActivity(Context context) {
        Intent intent = new Intent(context, OccupationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_white_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "职业大全";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(OccupationActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = OccupationFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //职业
            OccupationFragment fragment = new OccupationFragment();
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!hasHandlerBackPressed()) {
            super.onBackPressed();
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
        String tag = OccupationFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //根据fragment的处理结果赋值
        if (fragmentByTag != null && fragmentByTag instanceof BackPressedHandler) {
            res = ((BackPressedHandler) fragmentByTag).hasHandlerBackPressed();
        }
        return res;
    }

}

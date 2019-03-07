package com.cheersmind.cheersgenie.features_v2.modules.exam.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ChooseCourseFragment;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskAddFragment;

/**
 * 确认选课
 */
public class ChooseCourseActivity extends BaseActivity {

    //模块ID
    private static final String MODULE_ID = "MODULE_ID";

    /**
     * 启动确认选课页面
     * @param context 上下文
     * @param moduleId 模块ID
     */
    public static void startChooseCourseActivity(Context context, String moduleId) {
        Intent intent = new Intent(context, ChooseCourseActivity.class);
        Bundle extras = new Bundle();
        extras.putString(MODULE_ID, moduleId);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_choose_course;
    }

    @Override
    protected String settingTitle() {
        return "确认选课";
    }


    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        String moduleId = getIntent().getStringExtra(MODULE_ID);

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = ChooseCourseFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //确认选课
            ChooseCourseFragment fragment = new ChooseCourseFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putString(MODULE_ID, moduleId);
            fragment.setArguments(bundle);
            //添加已完成的测评fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

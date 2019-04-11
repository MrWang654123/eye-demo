package com.cheersmind.cheersgenie.features_v2.modules.exam.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ChooseCourseFragment;

/**
 * 确认选科
 */
public class ChooseCourseActivity extends BaseActivity {

    /**
     * 启动确认选课页面
     * @param childExamId 孩子测评Id
     * @param context 上下文
     */
    public static void startChooseCourseActivity(Context context,  String childExamId) {
        Intent intent = new Intent(context, ChooseCourseActivity.class);
        intent.putExtra(DtoKey.CHILD_EXAM_ID, childExamId);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_choose_course;
    }

    @Override
    protected String settingTitle() {
        return "确认选科";
    }


    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        //获取数据
        String childExamId = getIntent().getStringExtra(DtoKey.CHILD_EXAM_ID);

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = ChooseCourseFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            ChooseCourseFragment fragment = new ChooseCourseFragment();
            Bundle bundle = new Bundle();
            bundle.putString(DtoKey.CHILD_EXAM_ID, childExamId);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

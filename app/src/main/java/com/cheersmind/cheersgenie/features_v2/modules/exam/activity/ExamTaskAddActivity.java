package com.cheersmind.cheersgenie.features_v2.modules.exam.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskAddFragment;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskFragment;

/**
 * 添加测评任务
 */
public class ExamTaskAddActivity extends BaseActivity {

    //模块ID
    private static final String MODULE_ID = "MODULE_ID";

    /**
     * 启动添加测评任务页面
     * @param context 上下文
     * @param moduleId 模块ID
     */
    public static void startExamTaskAddActivity(Context context, String moduleId) {
        Intent intent = new Intent(context, ExamTaskAddActivity.class);
        Bundle extras = new Bundle();
        extras.putString(MODULE_ID, moduleId);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_exam_task_add;
    }

    @Override
    protected String settingTitle() {
        return "新增任务列表";
    }


    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        String moduleId = getIntent().getStringExtra(MODULE_ID);

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = ExamTaskAddFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //添加测评任务
            ExamTaskAddFragment fragment = new ExamTaskAddFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putString(MODULE_ID, moduleId);
            fragment.setArguments(bundle);
            //添加已完成的测评fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

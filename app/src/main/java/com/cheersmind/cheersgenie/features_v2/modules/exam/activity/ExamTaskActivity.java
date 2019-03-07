package com.cheersmind.cheersgenie.features_v2.modules.exam.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskFragment;

/**
 * 测评任务
 */
public class ExamTaskActivity extends BaseActivity {

    /**
     * 启动测评任务页面
     * @param context 上下文
     * @param childModuleId 孩子模块ID
     * @param moduleTitle 模块名称
     */
    public static void startExamTaskActivity(Context context, String childModuleId, String moduleTitle) {
        Intent intent = new Intent(context, ExamTaskActivity.class);
        Bundle extras = new Bundle();
        extras.putString(DtoKey.CHILD_MODULE_ID, childModuleId);
        extras.putString(DtoKey.MODULE_TITLE, moduleTitle);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_exam_task;
    }

    @Override
    protected String settingTitle() {
        return "任务列表";
    }


    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        String childModuleId = getIntent().getStringExtra(DtoKey.CHILD_MODULE_ID);
        String moduleTitle = getIntent().getStringExtra(DtoKey.MODULE_TITLE);
        //设置title
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(moduleTitle);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = ExamTaskFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //测评任务
            ExamTaskFragment fragment = new ExamTaskFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putString(DtoKey.CHILD_MODULE_ID, childModuleId);
            fragment.setArguments(bundle);
            //添加已完成的测评fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

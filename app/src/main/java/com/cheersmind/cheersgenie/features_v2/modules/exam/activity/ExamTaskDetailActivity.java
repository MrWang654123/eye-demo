package com.cheersmind.cheersgenie.features_v2.modules.exam.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskDetailFragment;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;

/**
 * 测评任务详情页
 */
public class ExamTaskDetailActivity extends BaseActivity {

    /**
     * 启动测评任务详情页面
     * @param context 上下文
     * @param examTask 测评任务
     */
    public static void startExamTaskDetailActivity(Context context, ExamTaskEntity examTask) {
        Intent intent = new Intent(context, ExamTaskDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(DtoKey.EXAM_TASK, examTask);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_exam_task_detail;
    }

    @Override
    protected String settingTitle() {
        return "任务详情";
    }


    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        ExamTaskEntity examTask = (ExamTaskEntity) getIntent().getSerializableExtra(DtoKey.EXAM_TASK);
        //设置title
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(examTask.getTask_name());
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = ExamTaskDetailFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //测评任务详情
            ExamTaskDetailFragment fragment = new ExamTaskDetailFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putSerializable(DtoKey.EXAM_TASK, examTask);
            fragment.setArguments(bundle);
            //添加已完成的测评fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

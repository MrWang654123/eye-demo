package com.cheersmind.cheersgenie.features_v2.modules.exam.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ObserveMajorFragment;

/**
 * 观察专业
 */
public class ObserveMajorActivity extends BaseActivity {

    /**
     * 启动观察专业页面
     * @param childExamId 孩子测评Id
     * @param isCompleteSelect 是否完成了最终选科，完成最终选科后重新进入专业观察表，隐藏添加和删除按钮
     * @param context 上下文
     */
    public static void startObserveMajorActivity(Context context, String childExamId,boolean isCompleteSelect) {
        Intent intent = new Intent(context, ObserveMajorActivity.class);
        intent.putExtra(DtoKey.CHILD_EXAM_ID, childExamId);
        intent.putExtra(DtoKey.IS_COMPLETE_SELECT_COURSE, isCompleteSelect);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "专业观察表";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
//        setStatusBarBackgroundColor(SelectCourseAssistantActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        //获取数据
        String childExamId = getIntent().getStringExtra(DtoKey.CHILD_EXAM_ID);
        boolean isCompleteSelectCourse = getIntent().getBooleanExtra("is_complete_select",false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = ObserveMajorFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            ObserveMajorFragment fragment = new ObserveMajorFragment();
            Bundle bundle = new Bundle();
            bundle.putString(DtoKey.CHILD_EXAM_ID, childExamId);
            bundle.putBoolean(DtoKey.IS_COMPLETE_SELECT_COURSE, isCompleteSelectCourse);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

package com.cheersmind.cheersgenie.features_v2.modules.exam.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.CourseRelateMajorFragment;

/**
 * 科目相关专业
 */
public class CourseRelateMajorActivity extends BaseActivity {

    /**
     * 启动科目相关专业页面
     * @param context 上下文
     * @param type 类型
     * @param courseCodeGroup 科目code组合
     * @param courseNameGroup 科目code组合
     */
    public static void startCourseRelateMajorActivity(Context context, int type, String courseCodeGroup, String courseNameGroup) {
        Intent intent = new Intent(context, CourseRelateMajorActivity.class);
        intent.putExtra(DtoKey.TYPE, type);
        intent.putExtra(DtoKey.COURSE_CODE_GROUP, courseCodeGroup);
        intent.putExtra(DtoKey.COURSE_NAME_GROUP, courseNameGroup);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_white_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "专业列表";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(CourseRelateMajorActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        //获取数据
        int type = getIntent().getIntExtra(DtoKey.TYPE, 1);
        String subjectGroup = getIntent().getStringExtra(DtoKey.COURSE_CODE_GROUP);
        String courseNameGroup = getIntent().getStringExtra(DtoKey.COURSE_NAME_GROUP);

        //标题
        if (tvToolbarTitle != null) {
            if (type == 1) {
                tvToolbarTitle.setText("可报考专业");
            } else if (type == 2) {
                tvToolbarTitle.setText("要求较高专业");
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = CourseRelateMajorFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //科目相关专业
            CourseRelateMajorFragment fragment = new CourseRelateMajorFragment();
            Bundle bundle = new Bundle();
            bundle.putString(DtoKey.COURSE_CODE_GROUP, subjectGroup);
            bundle.putInt(DtoKey.TYPE, type);
            bundle.putString(DtoKey.COURSE_NAME_GROUP, courseNameGroup);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

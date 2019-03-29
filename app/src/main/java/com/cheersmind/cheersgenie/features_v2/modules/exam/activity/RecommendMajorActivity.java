package com.cheersmind.cheersgenie.features_v2.modules.exam.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.RecommendMajorFragment;

/**
 * 推荐专业
 */
public class RecommendMajorActivity extends BaseActivity {

    /**
     * 启动推荐专业页面
     * @param childExamId 孩子测评Id
     * @param context 上下文
     */
    public static void startRmdMajorActivity(Context context, String childExamId) {
        Intent intent = new Intent(context, RecommendMajorActivity.class);
        intent.putExtra(DtoKey.CHILD_EXAM_ID, childExamId);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_white_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "选科助手";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(RecommendMajorActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        //获取数据
        String childExamId = getIntent().getStringExtra(DtoKey.CHILD_EXAM_ID);

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = RecommendMajorFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //推荐专业
            RecommendMajorFragment fragment = new RecommendMajorFragment();
            Bundle bundle = new Bundle();
            bundle.putString(DtoKey.CHILD_EXAM_ID, childExamId);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

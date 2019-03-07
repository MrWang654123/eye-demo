package com.cheersmind.cheersgenie.features_v2.modules.major.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorEntity;
import com.cheersmind.cheersgenie.features_v2.modules.major.fragment.MajorDetailFragment;
import com.cheersmind.cheersgenie.features_v2.modules.major.fragment.MajorFragment;

/**
 * 专业详情
 */
public class MajorDetailActivity extends BaseActivity {

    /**
     * 启动专业页面
     * @param context 上下文
     * @param major 专业
     */
    public static void startMajorActivity(Context context, MajorEntity major) {
        Intent intent = new Intent(context, MajorDetailActivity.class);
        intent.putExtra(DtoKey.MAJOR, major);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_white_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "专业详情";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(MajorDetailActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        //专业ID
        MajorEntity major = (MajorEntity) getIntent().getSerializableExtra(DtoKey.MAJOR);

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = MajorDetailFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //专业详情
            MajorDetailFragment fragment = new MajorDetailFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putSerializable(DtoKey.MAJOR, major);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

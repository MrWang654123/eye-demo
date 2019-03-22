package com.cheersmind.cheersgenie.features_v2.modules.occupation.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorItem;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationItem;
import com.cheersmind.cheersgenie.features_v2.modules.major.fragment.MajorDetailFragment;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.fragment.OccupationDetailFragment;

/**
 * 行业详情
 */
public class OccupationDetailActivity extends BaseActivity {

    /**
     * 启动行业详情页面
     * @param context 上下文
     * @param occupationItem 行业
     */
    public static void startOccupationDetailActivity(Context context, OccupationItem occupationItem) {
        Intent intent = new Intent(context, OccupationDetailActivity.class);
        intent.putExtra(DtoKey.OCCUPATION, occupationItem);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_white_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "职业详情";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(OccupationDetailActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        //行业
        OccupationItem occupationItem = (OccupationItem) getIntent().getSerializableExtra(DtoKey.OCCUPATION);

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = OccupationDetailFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //行业详情
            OccupationDetailFragment fragment = new OccupationDetailFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putSerializable(DtoKey.OCCUPATION, occupationItem);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

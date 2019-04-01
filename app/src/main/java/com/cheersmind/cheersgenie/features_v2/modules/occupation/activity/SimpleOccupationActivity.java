package com.cheersmind.cheersgenie.features_v2.modules.occupation.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.fragment.SimpleOccupationFragment;

/**
 * 职业列表（用于直接从Act26大门类跳转过来）
 */
public class SimpleOccupationActivity extends BaseActivity {

    /**
     * * 启动职业页面
     * @param context 上下文
     */
    public static void startOccupationActivity(Context context, OccupationCategory category) {
        Intent intent = new Intent(context, SimpleOccupationActivity.class);
        intent.putExtra(DtoKey.OCCUPATION_CATEGORY, category);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_white_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "职业列表";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(SimpleOccupationActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        OccupationCategory category = (OccupationCategory) getIntent().getSerializableExtra(DtoKey.OCCUPATION_CATEGORY);

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = SimpleOccupationFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //职业
            SimpleOccupationFragment fragment = new SimpleOccupationFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(DtoKey.OCCUPATION_CATEGORY, category);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

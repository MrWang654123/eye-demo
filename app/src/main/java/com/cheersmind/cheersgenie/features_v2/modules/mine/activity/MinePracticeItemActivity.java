package com.cheersmind.cheersgenie.features_v2.modules.mine.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.entity.PracticeEntity;
import com.cheersmind.cheersgenie.features_v2.modules.mine.fragment.MinePracticeItemFragment;

/**
 * 实践子项
 */
public class MinePracticeItemActivity extends BaseActivity {

    /**
     * 启动我的实践子项页面
     * @param context 上下文
     */
    public static void startMinePracticeItemActivity(Context context, PracticeEntity practice) {
        Intent intent = new Intent(context, MinePracticeItemActivity.class);
        intent.putExtra(DtoKey.PRACTICE, practice);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_white_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "实践明细";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(MinePracticeItemActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        PracticeEntity practice = (PracticeEntity) getIntent().getSerializableExtra(DtoKey.PRACTICE);
        //设置title
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(practice.getArticleTitle().substring(0,7));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = MinePracticeItemFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //我的实践子项
            MinePracticeItemFragment fragment = new MinePracticeItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString(DtoKey.PRACTICE_ID, practice.getId());
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

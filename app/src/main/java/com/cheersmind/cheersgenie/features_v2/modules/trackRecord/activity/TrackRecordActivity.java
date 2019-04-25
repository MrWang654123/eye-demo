package com.cheersmind.cheersgenie.features_v2.modules.trackRecord.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment.TrackRecordFragment;

/**
 * 成长档案
 */
public class TrackRecordActivity extends BaseActivity {

    /**
     * 启动成长档案页面
     * @param childExamId 孩子测评ID
     * @param context 上下文
     */
    public static void startTrackRecordActivity(Context context, String childExamId) {
        Intent intent = new Intent(context, TrackRecordActivity.class);
        intent.putExtra(DtoKey.CHILD_EXAM_ID, childExamId);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "成长档案";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
//        setStatusBarBackgroundColor(TrackRecordActivity.this, getResources().getColor(R.color.white));
    }

    @Override
    protected void onInitData() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = TrackRecordFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //成长档案
            TrackRecordFragment fragment = new TrackRecordFragment();
            //添加孩子测评ID
            String childExamId = getIntent().getStringExtra(DtoKey.CHILD_EXAM_ID);
            Bundle bundle = new Bundle();
            bundle.putString(DtoKey.CHILD_EXAM_ID, childExamId);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

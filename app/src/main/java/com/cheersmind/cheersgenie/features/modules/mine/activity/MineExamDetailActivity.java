package com.cheersmind.cheersgenie.features.modules.mine.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.HistoryExamDetailFragment;
import com.cheersmind.cheersgenie.features.modules.explore.fragment.CategoryTabItemFragment;
import com.cheersmind.cheersgenie.features.modules.mine.fragment.MineExamDetailFragment;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

/**
 * 历史测评明细页面
 */
public class MineExamDetailActivity extends BaseActivity {

    //测评ID
    public static final String EXAM_ID = "exam_id";
    //测评状态
    public static final String EXAM_STATUS = "exam_status";
    //测评名称
    public static final String EXAM_NAME = "exam_name";


    /**
     * 启动历史测评明细页面
     * @param context 上下文
     * @param examId 测评ID
     */
    public static void startMineExamDetailActivity(Context context, String examId, int examStatus, String examName) {
        Intent intent = new Intent(context, MineExamDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putString(EXAM_ID, examId);
        extras.putInt(EXAM_STATUS, examStatus);
        extras.putString(EXAM_NAME, examName);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_mine_exam_detail;
    }

    @Override
    protected String settingTitle() {
        return "我的智评";
    }


    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        //测评ID
        String examId = getIntent().getStringExtra(EXAM_ID);
        if (TextUtils.isEmpty(examId)) {
            ToastUtil.showShort(getApplication(), getResources().getString(R.string.operate_fail));
            finish();
            return;
        }
        //测评状态
        int examStatus = getIntent().getIntExtra(EXAM_STATUS, Dictionary.EXAM_STATUS_OVER);
        //测评名称
        String examName = getIntent().getStringExtra(EXAM_NAME);
        //设置页面标题为测评名称
        settingTitle(examName);

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = MineExamDetailFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //已完成的测评
            MineExamDetailFragment fragment = new MineExamDetailFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putString(EXAM_ID, examId);
            bundle.putInt(EXAM_STATUS, examStatus);
            bundle.putString(EXAM_NAME, examName);
            fragment.setArguments(bundle);
            //添加已完成的测评fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

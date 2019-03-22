package com.cheersmind.cheersgenie.features_v2.modules.exam.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamReportFragment;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskDetailFragment;

/**
 * 测评报告页
 */
public class ExamReportActivity extends BaseActivity {

    /**
     * 启动测评报告页
     * @param context 上下文
     * @param dto 报告dto
     */
    public static void startExamReportActivity(Context context, ExamReportDto dto) {
        Intent intent = new Intent(context, ExamReportActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(DtoKey.EXAM_REPORT_DTO, dto);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_title_fragment;
    }

    @Override
    protected String settingTitle() {
        return "报告";
    }


    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        ExamReportDto dto = (ExamReportDto) getIntent().getSerializableExtra(DtoKey.EXAM_REPORT_DTO);
        //设置title
        if (tvToolbarTitle != null
                && !TextUtils.isEmpty(dto.getRelationName())) {
            tvToolbarTitle.setText(dto.getRelationName());
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = ExamReportFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            ExamReportFragment fragment = new ExamReportFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putSerializable(DtoKey.EXAM_REPORT_DTO, dto);
            fragment.setArguments(bundle);
            //添加已完成的测评fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

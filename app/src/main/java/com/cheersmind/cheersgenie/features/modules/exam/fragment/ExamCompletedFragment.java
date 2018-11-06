package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;

/**
 * 已完成的测评
 */
public class ExamCompletedFragment extends ExamBaseFragment {

    @Override
    public void onInitView(View contentView) {
        //完成状态
        topicStatus = Dictionary.TOPIC_STATUS_COMPLETE;
        super.onInitView(contentView);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_report));
    }

}


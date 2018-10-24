package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.view.View;

import com.cheersmind.cheersgenie.features.constant.Dictionary;

/**
 * 正在进行的测评
 */
public class ExamDoingFragment extends ExamCompletedFragment {

    @Override
    public void onInitView(View contentView) {
        //未完成状态
        topicStatus = Dictionary.TOPIC_STATUS_INCOMPLETE;

        super.onInitView(contentView);
    }
}

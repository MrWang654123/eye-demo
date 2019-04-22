package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.MajorItem;

import java.util.List;

/**
 * 开设专业recycler适配器
 */
public class SetupMajorRecyclerAdapter extends BaseQuickAdapter<MajorItem, BaseViewHolder> {

    public SetupMajorRecyclerAdapter(Context context, int layoutResId, @Nullable List<MajorItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MajorItem item) {
        helper.setText(R.id.tv_title, item.getMajor_name());
        helper.setText(R.id.tv_major_level, item.getAssessment_level());
        //高要求学科
        if (!TextUtils.isEmpty(item.getSubjects_required())) {
            helper.getView(R.id.tv_high_require_course).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_high_require_course, item.getSubjects_required());
        } else {
            helper.getView(R.id.tv_high_require_course).setVisibility(View.GONE);
        }
    }

}

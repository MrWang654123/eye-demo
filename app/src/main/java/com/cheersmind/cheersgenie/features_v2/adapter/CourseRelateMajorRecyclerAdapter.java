package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;

import java.util.List;

/**
 * 科目相关专业recycler适配器
 */
public class CourseRelateMajorRecyclerAdapter extends BaseQuickAdapter<RecommendMajor, BaseViewHolder> {

    public CourseRelateMajorRecyclerAdapter(Context context, int layoutResId, @Nullable List<RecommendMajor> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecommendMajor item) {
        //标题
        helper.setText(R.id.tv_title, item.getMajor_name());
    }

}

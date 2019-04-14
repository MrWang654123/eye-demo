package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 推荐专业recycler适配器
 */
public class RecommendMajorRecyclerAdapter extends BaseQuickAdapter<RecommendMajor, BaseViewHolder> {

    public RecommendMajorRecyclerAdapter(Context context, int layoutResId, @Nullable List<RecommendMajor> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecommendMajor item) {
        //标题
        helper.setText(R.id.tv_title, item.getMajor_name());

        //选中图标
        SimpleDraweeView ivSelect = helper.getView(R.id.iv_select);
        if (item.isSelected()) {
            ivSelect.setImageResource(R.drawable.check_box_outline);

        } else {
            ivSelect.setImageResource(R.drawable.check_box_outline_bl);
        }

        helper.addOnClickListener(R.id.iv_select);
    }

}

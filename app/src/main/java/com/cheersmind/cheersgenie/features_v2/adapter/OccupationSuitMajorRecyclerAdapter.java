package com.cheersmind.cheersgenie.features_v2.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.MajorItem;

import java.util.List;

/**
 * 行业的对口专业recycler适配器
 */
public class OccupationSuitMajorRecyclerAdapter extends BaseQuickAdapter<MajorItem, BaseViewHolder> {

    public OccupationSuitMajorRecyclerAdapter(int layoutResId, @Nullable List<MajorItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MajorItem item) {
        helper.setText(R.id.tv_title, item.getMajor_name());
    }

}

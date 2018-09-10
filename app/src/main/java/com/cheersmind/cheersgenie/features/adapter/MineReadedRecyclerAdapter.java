package com.cheersmind.cheersgenie.features.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;

import java.util.List;

/**
 * “我看过的”recycler适配器
 */
public class MineReadedRecyclerAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

    public MineReadedRecyclerAdapter(int layoutResId, @Nullable List<Object> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Object item) {
        int position = helper.getLayoutPosition();
        int headCount = getHeaderLayoutCount();
        helper.setText(R.id.tv_type, (position + 1 - headCount) + "-学习策略");
    }
}

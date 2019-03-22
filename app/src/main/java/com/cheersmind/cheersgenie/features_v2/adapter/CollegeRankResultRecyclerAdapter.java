package com.cheersmind.cheersgenie.features_v2.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRankResultItem;

import java.util.List;

/**
 * 大学排名结果recycler适配器
 */
public class CollegeRankResultRecyclerAdapter extends BaseQuickAdapter<CollegeRankResultItem, BaseViewHolder> {

    public CollegeRankResultRecyclerAdapter(int layoutResId, @Nullable List<CollegeRankResultItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollegeRankResultItem item) {
        helper.setText(R.id.tv_rank_key, item.getName());
        helper.setText(R.id.tv_rank_value, String.valueOf(item.getValue()));
    }

}

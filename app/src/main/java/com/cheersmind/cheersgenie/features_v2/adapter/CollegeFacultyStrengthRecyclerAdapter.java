package com.cheersmind.cheersgenie.features_v2.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.FacultyStrengthItemChild;

import java.util.List;

/**
 * 大学排名结果recycler适配器
 */
public class CollegeFacultyStrengthRecyclerAdapter extends BaseQuickAdapter<FacultyStrengthItemChild, BaseViewHolder> {

    public CollegeFacultyStrengthRecyclerAdapter(int layoutResId, @Nullable List<FacultyStrengthItemChild> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FacultyStrengthItemChild item) {
        helper.setText(R.id.tv_count, String.valueOf(item.getTotal()));
        helper.setText(R.id.tv_title, String.valueOf(item.getName()));
    }

}

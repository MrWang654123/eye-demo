package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationItem;

import java.util.List;

/**
 * 职业recycler适配器
 */
public class OccupationRecyclerAdapter extends BaseQuickAdapter<OccupationItem, BaseViewHolder> {

    public OccupationRecyclerAdapter(Context context, int layoutResId, @Nullable List<OccupationItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OccupationItem item) {
        helper.setText(R.id.tv_title, item.getOccupation_name());
    }

}

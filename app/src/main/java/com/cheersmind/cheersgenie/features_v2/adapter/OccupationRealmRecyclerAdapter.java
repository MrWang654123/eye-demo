package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.dto.OccupationDto;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationRealm;

import java.util.List;

/**
 * 职业领域recycler适配器
 */
public class OccupationRealmRecyclerAdapter extends BaseQuickAdapter<OccupationRealm, BaseViewHolder> {

    private Context context;

    private OccupationDto dto;

    public OccupationRealmRecyclerAdapter(Context context, int layoutResId, @Nullable List<OccupationRealm> data, OccupationDto dto) {
        super(layoutResId, data);
        this.context = context;
        this.dto = dto;
    }

    @Override
    protected void convert(BaseViewHolder helper, OccupationRealm item) {
        ((TextView)helper.getView(R.id.tv_title)).setTextColor(
                ContextCompat.getColor(context, R.color.color_555555));
        //选中变色
        if (dto != null
                && dto.getRealm() != null
                && !TextUtils.isEmpty(dto.getRealm().getName())) {

            if (dto.getRealm().getName().equals(item.getName())) {
                ((TextView) helper.getView(R.id.tv_title)).setTextColor(
                        ContextCompat.getColor(context, R.color.color_e46c3e));
            }
        }
        helper.setText(R.id.tv_title, item.getName());
    }

}

package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 开设院校recycler适配器
 */
public class SetupCollegeRecyclerAdapter extends BaseQuickAdapter<CollegeEntity, BaseViewHolder> {

    public SetupCollegeRecyclerAdapter(Context context, int layoutResId, @Nullable List<CollegeEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollegeEntity item) {
        //中文名称
        helper.setText(R.id.tv_title, item.getCn_name());

        //Logo
        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
        imageView.setImageURI(item.getLogo_url());

        //所在地
        String location = "";
        //省份
        if (!TextUtils.isEmpty(item.getState())) {
            location += item.getState();
            //城市
            if (!TextUtils.isEmpty(item.getCity_data())
                    && !item.getState().equals(item.getCity_data())) {
                location += ("-" + item.getCity_data());
            }
        }

        if (!TextUtils.isEmpty(location)) {
            helper.getView(R.id.tv_location).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_location, location);
        } else {
            helper.getView(R.id.tv_location).setVisibility(View.GONE);
        }

        //专业评级
        if (item.getMajor() != null && !TextUtils.isEmpty(item.getMajor().getAssessment_level())) {
            helper.getView(R.id.tv_major_level).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_major_level, item.getMajor().getAssessment_level());
        } else {
            helper.getView(R.id.tv_major_level).setVisibility(View.GONE);
        }
    }

}

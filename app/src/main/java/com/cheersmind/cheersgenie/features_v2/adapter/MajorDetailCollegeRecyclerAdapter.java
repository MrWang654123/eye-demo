package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 专业的开设院校recycler适配器
 */
public class MajorDetailCollegeRecyclerAdapter extends BaseQuickAdapter<CollegeEntity, BaseViewHolder> {

    public MajorDetailCollegeRecyclerAdapter(Context context, int layoutResId, @Nullable List<CollegeEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollegeEntity item) {
        //中文名称
        helper.setText(R.id.tv_title, item.getCn_name());

        //Logo
        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
        imageView.setImageURI(item.getLogo_url());
    }

}

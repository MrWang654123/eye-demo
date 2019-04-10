package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MedalEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 我的勋章recycler适配器
 */
public class MineMedalRecyclerAdapter extends BaseQuickAdapter<MedalEntity, BaseViewHolder> {

    public MineMedalRecyclerAdapter(Context context, int layoutResId, @Nullable List<MedalEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MedalEntity item) {
        //名称
        helper.setText(R.id.tv_title, item.getMedal_name());

//        if (item.getStatus() == 1) {
//            //图标
//            SimpleDraweeView imageView = helper.getView(R.id.iv_main);
//            imageView.setImageURI(item.getIcon());
//        } else {
//            //图标
//            SimpleDraweeView imageView = helper.getView(R.id.iv_main);
//            imageView.setImageURI(item.getIcon());
//            imageView.setActualImageResource(R.drawable.medal_default);
//        }

        //图标
        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
        imageView.setImageURI(item.getIcon());
    }

}

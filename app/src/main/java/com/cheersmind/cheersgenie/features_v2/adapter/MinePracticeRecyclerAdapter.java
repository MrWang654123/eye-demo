package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.PracticeEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 我的实践recycler适配器
 */
public class MinePracticeRecyclerAdapter extends BaseQuickAdapter<PracticeEntity, BaseViewHolder> {

    public MinePracticeRecyclerAdapter(Context context, int layoutResId, @Nullable List<PracticeEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PracticeEntity item) {
        //标题
        helper.setText(R.id.tv_title, item.getArticleTitle().length() > 6 ? item.getArticleTitle().substring(0, 6) : item.getArticleTitle());

        //主图
        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
        imageView.setImageURI(item.getArticleImg());
    }

}

package com.cheersmind.cheersgenie.features.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;

import java.util.List;

/**
 * 首页recycler适配器
 */
public class HomeRecyclerAdapter extends BaseQuickAdapter<SimpleArticleEntity, BaseViewHolder> {

    private Context context;

    public HomeRecyclerAdapter(Context context ,int layoutResId, @Nullable List<SimpleArticleEntity> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, SimpleArticleEntity item) {
        int position = helper.getLayoutPosition();
        int headCount = getHeaderLayoutCount();

        //标题
        helper.setText(R.id.tv_article_title, item.getArticleTitle());
        //简介
        if (!TextUtils.isEmpty(item.getSummary())) {
            helper.setText(R.id.tv_article_desc, item.getSummary());
        } else {
            helper.getView(R.id.tv_article_desc).setVisibility(View.GONE);
        }
        //主图
        String url = item.getArticleImg();
        ImageView imageView = helper.getView(R.id.iv_main);
        Glide.with(context)
                .load(url)
                .thumbnail(0.5f)
                .apply(QSApplication.getDefaultOptions())
                .into(imageView);

        //测试编号
        String number = (position + 1 - headCount) +"";

        //标签（目前用的是类型）
        if (item.getCategory() != null && !TextUtils.isEmpty(item.getCategory().getName())) {
//            helper.setText(R.id.tv_tag, number+ "、" + item.getCategory().getName());
            helper.setText(R.id.tv_tag, item.getCategory().getName());
        } else {
            helper.getView(R.id.tv_tag).setVisibility(View.GONE);
        }

        //阅读数量
        if (item.getPageView() > 0) {
            helper.setText(R.id.tv_read_count, item.getPageView() + "");
        } else {
            helper.getView(R.id.rl_read).setVisibility(View.GONE);
        }

        //有关联测评
        if (item.getIsReferenceTest() == Dictionary.ARTICLE_IS_REFERENCE_EXAM_YES) {
            //测过人数大于0才显示
            if (item.getTestCount() > 0) {
                //评测过的数量
                helper.setText(R.id.tv_evaluation_count, item.getTestCount() + "");
            } else {
                helper.getView(R.id.rl_evaluation).setVisibility(View.GONE);
            }
        } else {
            helper.getView(R.id.rl_evaluation).setVisibility(View.GONE);
        }

        //收藏状态初始化
        ImageView ivFavorite = helper.getView(R.id.iv_favorite);
        if (item.isFavorite()) {
            //收藏状态
            ivFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_do));
        } else {
            //未收藏状态
            ivFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_not));
        }
        //收藏点击监听
        helper.addOnClickListener(R.id.iv_favorite);

    }
}

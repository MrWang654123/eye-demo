package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.SimpleArticleEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 发现页Recycler适配器
 */
public class DiscoverRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public final static int LAYOUT_TYPE_VIDEO = 1;//视频
    public final static int LAYOUT_TYPE_ARTICLE_COMMON = 2;//通用文章
    public final static int LAYOUT_TYPE_ARTICLE_THREE_COVER = 3;//文章3张封面图

    private Context context;

    public DiscoverRecyclerAdapter(Context context, List<MultiItemEntity> data) {
        super(data);
        this.context = context;

        addItemType(LAYOUT_TYPE_VIDEO, R.layout.recycleritem_discover_video);
        addItemType(LAYOUT_TYPE_ARTICLE_COMMON, R.layout.recycleritem_discover_article_common);
        addItemType(LAYOUT_TYPE_ARTICLE_THREE_COVER, R.layout.recycleritem_discover_article_mutil_img);
    }


    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity entity) {
        switch (helper.getItemViewType()) {
            //视频
            case LAYOUT_TYPE_VIDEO: {
                SimpleArticleEntity item = (SimpleArticleEntity) entity;

                //标题
                helper.setText(R.id.tv_article_title, item.getArticleTitle());

                //主图
                SimpleDraweeView imageView = helper.getView(R.id.iv_main);
                imageView.setImageURI(item.getArticleImg());

                //播放键
                if (item.getContentType() == Dictionary.ARTICLE_TYPE_VIDEO) {
                    helper.getView(R.id.iv_play).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.iv_play).setVisibility(View.GONE);
                }

                //标签（目前用的是类型）
                if (item.getCategory() != null && !TextUtils.isEmpty(item.getCategory().getName())) {
                    helper.getView(R.id.tv_tag).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_tag, item.getCategory().getName());
                } else {
                    helper.getView(R.id.tv_tag).setVisibility(View.GONE);
                }

                //阅读数量
                if (item.getPageView() > 0) {
                    helper.getView(R.id.tv_read_count).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_read_count, item.getPageView() + "阅");
                } else {
                    helper.getView(R.id.tv_read_count).setVisibility(View.GONE);
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
                break;
            }
            //通用文章
            case LAYOUT_TYPE_ARTICLE_COMMON: {
                SimpleArticleEntity item = (SimpleArticleEntity) entity;

                //标题
                helper.setText(R.id.tv_article_title, item.getArticleTitle());

                //主图
                SimpleDraweeView imageView = helper.getView(R.id.iv_main);
                imageView.setImageURI(item.getArticleImg());

                //标签（目前用的是类型）
                if (item.getCategory() != null && !TextUtils.isEmpty(item.getCategory().getName())) {
                    helper.getView(R.id.tv_tag).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_tag, item.getCategory().getName());
                } else {
                    helper.getView(R.id.tv_tag).setVisibility(View.GONE);
                }

                //阅读数量
                if (item.getPageView() > 0) {
                    helper.getView(R.id.tv_read_count).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_read_count, item.getPageView() + "阅");
                } else {
                    helper.getView(R.id.tv_read_count).setVisibility(View.GONE);
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
                break;
            }
            //文章3张封面图
            case LAYOUT_TYPE_ARTICLE_THREE_COVER: {
                SimpleArticleEntity item = (SimpleArticleEntity) entity;

                //标题
                helper.setText(R.id.tv_article_title, item.getArticleTitle());

                //多图
                SimpleDraweeView imageView1 = helper.getView(R.id.iv_main1);
                SimpleDraweeView imageView2 = helper.getView(R.id.iv_main2);
                SimpleDraweeView imageView3 = helper.getView(R.id.iv_main3);
                if (!TextUtils.isEmpty(item.getArticleImgs())) {
                    String[] split = item.getArticleImgs().split(",");
                    imageView1.setImageURI(split[0]);
                    if (split.length > 1) {
                        imageView2.setImageURI(split[1]);
                    }
                    if (split.length > 2) {
                        imageView3.setImageURI(split[2]);
                    }
                }

                //标签（目前用的是类型）
                if (item.getCategory() != null && !TextUtils.isEmpty(item.getCategory().getName())) {
                    helper.getView(R.id.tv_tag).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_tag, item.getCategory().getName());
                } else {
                    helper.getView(R.id.tv_tag).setVisibility(View.GONE);
                }

                //阅读数量
                if (item.getPageView() > 0) {
                    helper.getView(R.id.tv_read_count).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_read_count, item.getPageView() + "阅");
                } else {
                    helper.getView(R.id.tv_read_count).setVisibility(View.GONE);
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
                break;
            }
        }
    }

}


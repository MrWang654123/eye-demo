package com.cheersmind.cheersgenie.features.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.holder.BaseHolder;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.main.entity.CommentEntity;

import java.util.List;

/**
 * 评论recycler适配器
 */
public class CommentAdapter extends BaseAdapter<CommentEntity> {

    private Context context;
    //默认Glide配置
    RequestOptions options;

    public CommentAdapter(Context context, int layoutId, List<CommentEntity> list) {
        super(layoutId, list);
        this.context = context;

//        options = new RequestOptions()
//                .circleCrop()//圆形
//                .skipMemoryCache(false)//不忽略内存
//                .placeholder(R.drawable.ico_head)//占位图
//                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
//                .diskCacheStrategy(DiskCacheStrategy.ALL);//磁盘缓存策略：缓存所有
        options = new RequestOptions()
                .circleCrop()//圆形
                .skipMemoryCache(true)//忽略内存
                .placeholder(R.drawable.ico_head)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.NONE);//磁盘缓存策略：不缓存
    }

    @Override
    protected void convert(BaseHolder holder, CommentEntity item) {
//            holder.setText(R.id.item_tv_title,item).setImageResource(R.id.image,R.drawable.ic_default);
        //用户信息（发布评论者）
        if (item.getUserData() != null) {
            //头像
            String avatar = item.getUserData().getAvatar();
            ImageView imageView = holder.getView(R.id.iv_profile);
            if (!TextUtils.isEmpty(avatar)) {
                GlideUrl glideUrl = new GlideUrl(avatar, new LazyHeaders.Builder()
                        .addHeader(Dictionary.PROFILE_HEADER_KEY, Dictionary.PROFILE_HEADER_VALUE)
                        .build());
                Glide.with(context)
                        .load(glideUrl)
//                    .thumbnail(0.5f)
                        .apply(options)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(R.drawable.ico_head)
//                    .thumbnail(0.5f)
                        .apply(options)
                        .into(imageView);
            }

            //用户名
            String userName = item.getUserData().getUserName();
            if (!TextUtils.isEmpty(userName)) {
                holder.setText(R.id.tv_username, item.getUserData().getUserName());
            } else {
                ((View)holder.getView(R.id.tv_username)).setVisibility(View.GONE);
            }
        }

        //发布时间
        if (!TextUtils.isEmpty(item.getCreateTime())) {
            holder.setText(R.id.tv_send_date, item.getCreateTime());
        } else {
            ((View)holder.getView(R.id.tv_send_date)).setVisibility(View.GONE);
        }

        //发布内容
        if (!TextUtils.isEmpty(item.getCommentInfo())) {
            holder.setText(R.id.tv_content, item.getCommentInfo());
        } else {
            ((View)holder.getView(R.id.tv_content)).setVisibility(View.GONE);
        }

    }
}

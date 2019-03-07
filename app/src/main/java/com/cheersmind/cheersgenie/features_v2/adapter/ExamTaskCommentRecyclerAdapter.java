package com.cheersmind.cheersgenie.features_v2.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.CommentEntity;

import java.util.List;

/**
 * 测评任务评论recycler适配器
 */
public class ExamTaskCommentRecyclerAdapter extends BaseQuickAdapter<CommentEntity, BaseViewHolder> {

    //默认Glide配置
    private RequestOptions options;

    public ExamTaskCommentRecyclerAdapter(int layoutResId, @Nullable List<CommentEntity> data) {
        super(layoutResId, data);

        options = new RequestOptions()
                .circleCrop()//圆形
                .skipMemoryCache(true)//忽略内存
                .placeholder(R.drawable.ico_head)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.NONE);//磁盘缓存策略：不缓存
    }

    @Override
    protected void convert(BaseViewHolder holder, CommentEntity item) {
        int position = holder.getLayoutPosition();
        //用户信息（发布评论者）
        if (item.getUserData() != null) {
            //头像
            String avatar = item.getUserData().getAvatar();
            ImageView imageView = holder.getView(R.id.iv_profile);
            if (!TextUtils.isEmpty(avatar)) {
                GlideUrl glideUrl = new GlideUrl(avatar, new LazyHeaders.Builder()
                        .addHeader(Dictionary.PROFILE_HEADER_KEY, Dictionary.PROFILE_HEADER_VALUE)
                        .build());
                Glide.with(getRecyclerView().getContext())
                        .load(glideUrl)
//                    .thumbnail(0.5f)
                        .apply(options)
                        .into(imageView);
            } else {
                Glide.with(getRecyclerView().getContext())
                        .load(R.drawable.ico_head)
//                    .thumbnail(0.5f)
                        .apply(options)
                        .into(imageView);
            }

            //昵称
            String nickName = item.getUserData().getNickName();
            if (!TextUtils.isEmpty(nickName)) {
                holder.setText(R.id.tv_username, nickName);
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


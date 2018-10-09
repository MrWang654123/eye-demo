package com.cheersmind.cheersgenie.features.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.main.util.DensityUtil;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 首页的banner holder
 */
public class BannerHomeHolder extends Holder<SimpleArticleEntity> {

    private Fragment fragment;
    //主图
    private ImageView ivMain;
    //播放键
    private ImageView ivPlay;
    //标题
    private TextView tvTitle;
    //类型
    private TextView tvType;


    //默认Glide处理参数
    private static RequestOptions defaultOptions;

    /**
     * 初始化默认Glide处理参数
     */
    private void initRequestOptions(Context context) {
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                new CenterCrop(),
                new RoundedCornersTransformation(DensityUtil.dip2px(context, 12), 0, RoundedCornersTransformation.CornerType.ALL));

        //默认Glide处理参数
        defaultOptions = new RequestOptions()
                .skipMemoryCache(false)//不忽略内存
                .placeholder(R.drawable.default_image_round_article_list)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//磁盘缓存策略：缓存所有
                .transform(multi);

    }


    public BannerHomeHolder(Fragment fragment, View itemView) {
        super(itemView);
        this.fragment = fragment;

        initRequestOptions(fragment.getContext());
    }

    @Override
    protected void initView(View itemView) {
        ivMain = itemView.findViewById(R.id.iv_main);
        ivPlay = itemView.findViewById(R.id.iv_play);
        tvTitle = itemView.findViewById(R.id.tv_title);
        tvType = itemView.findViewById(R.id.tv_type);
    }

    @Override
    public void updateUI(SimpleArticleEntity entity) {
        //主图
        Glide.with(fragment)
                .load(entity.getArticleImg())
//                .thumbnail(0.5f)//缩略图
                .apply(defaultOptions)
                .into(ivMain);

        //播放键
        if (entity.getContentType() == Dictionary.ARTICLE_TYPE_VIDEO) {
            ivPlay.setVisibility(View.VISIBLE);
        } else {
            ivPlay.setVisibility(View.GONE);
        }

        //标题
        tvTitle.setText(entity.getArticleTitle());

        //类型
        if (entity.getContentType() == Dictionary.ARTICLE_TYPE_VIDEO) {
            tvType.setText("视频");
        } else {
            tvType.setText("文章");
        }

    }

}


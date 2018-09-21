package com.cheersmind.cheersgenie.features.holder;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.main.util.DensityUtil;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 首页的banner holder
 */
public class BannerHomeHolder extends Holder<SimpleArticleEntity> {

    private Fragment fragment;
    //主图
    private ImageView imageView;
    //标题
    private TextView tvTitle;


    //默认Glide处理参数
    private static RequestOptions defaultOptions;

    /**
     * 初始化默认Glide处理参数
     */
    private void initRequestOptions() {
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                new CenterCrop(),
                new RoundedCornersTransformation(DensityUtil.dip2px(fragment.getActivity(), 12), 0, RoundedCornersTransformation.CornerType.ALL));

        //默认Glide处理参数
        defaultOptions = new RequestOptions();
        defaultOptions.skipMemoryCache(false);//不忽略内存
        defaultOptions.placeholder(R.drawable.default_image_round);//占位图
        defaultOptions.dontAnimate();//Glide默认是渐变动画，设置dontAnimate()不要动画
        defaultOptions.diskCacheStrategy(DiskCacheStrategy.ALL);//磁盘缓存策略：缓存所有
        defaultOptions.transform(multi);

    }


    public BannerHomeHolder(Fragment fragment, View itemView) {
        super(itemView);
        this.fragment = fragment;

        initRequestOptions();
    }

    @Override
    protected void initView(View itemView) {
        imageView = itemView.findViewById(R.id.ivPost);
        tvTitle = itemView.findViewById(R.id.tv_title);
    }

    @Override
    public void updateUI(SimpleArticleEntity entity) {
        //主图
        Glide.with(fragment)
                .load(entity.getArticleImg())
                .thumbnail(0.5f)//缩略图
                .apply(defaultOptions)
                .into(imageView);

        //标题
        tvTitle.setText(entity.getArticleTitle());

    }

}


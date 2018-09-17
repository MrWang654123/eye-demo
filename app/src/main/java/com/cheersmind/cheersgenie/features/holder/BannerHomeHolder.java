package com.cheersmind.cheersgenie.features.holder;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;

/**
 * 首页的banner holder
 */
public class BannerHomeHolder extends Holder<SimpleArticleEntity> {

    private ImageView imageView;
    private Fragment fragment;

    public BannerHomeHolder(Fragment fragment, View itemView) {
        super(itemView);
        this.fragment = fragment;
    }

    @Override
    protected void initView(View itemView) {
        imageView =itemView.findViewById(R.id.ivPost);
    }

    @Override
    public void updateUI(SimpleArticleEntity entity) {
//        if(!TextUtils.isEmpty(url) && !url.equals(imageView.getTag(R.id.ivPost))){
            Glide.with(fragment)
                    .load(entity.getArticleImg())
                    .thumbnail(0.5f)//缩略图
                    .apply(QSApplication.getDefaultOptions())
                    .into(imageView);
//            imageView.setTag(R.id.ivPost,url);
//        }
    }

}


package com.cheersmind.cheersgenie.features.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 广告栏viewPager的适配器
 */
public class BannerPageAdapter extends PagerAdapter {

    private Fragment fragment;
    private List<SimpleArticleEntity> list;
    private LayoutInflater inflater;

    //默认Glide处理参数
//    private static RequestOptions defaultOptions;

    //页面点击监听
    private OnPageClickListener listener;


    public BannerPageAdapter(Fragment fragment, List<SimpleArticleEntity> list) {
        this.fragment = fragment;
        this.list = list;
        inflater = LayoutInflater.from(fragment.getContext());
        //初始化Glide处理参数
//        initRequestOptions(fragment);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = inflater.inflate(R.layout.banneritem_home, container, false);
        SimpleDraweeView ivMain = itemView.findViewById(R.id.iv_main);
        ImageView ivPlay = itemView.findViewById(R.id.iv_play);
        TextView tvTitle = itemView.findViewById(R.id.tv_title);
        TextView tvType = itemView.findViewById(R.id.tv_type);

        SimpleArticleEntity entity = list.get(position);

        //主图
//        Glide.with(fragment)
//                .load(entity.getArticleImg())
////                .thumbnail(0.5f)//缩略图
//                .apply(defaultOptions)
//                .into(ivMain);
        ivMain.setImageURI(entity.getArticleImg());

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

        //页面点击事件
        itemView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                if (listener != null) {
                    listener.onPageClick(position);
                }
            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


//    /**
//     * 初始化默认Glide处理参数
//     */
//    private void initRequestOptions(Fragment fragment) {
//        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
//                new CenterCrop(),
//                new RoundedCornersTransformation(DensityUtil.dip2px(fragment.getContext(), 3), 0, RoundedCornersTransformation.CornerType.ALL));
//
//        //默认Glide处理参数
//        defaultOptions = new RequestOptions()
//                .transform(multi)
//                .skipMemoryCache(false)//不忽略内存
//                .placeholder(R.drawable.default_image_round_article_list)//占位图
//                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//磁盘缓存策略：缓存所有
//                ;
////                .centerCrop();
//    }


    /**
     * 页面点击监听
     */
    public interface OnPageClickListener {
        //页面被点击
        void onPageClick(int position);
    }

    public OnPageClickListener getListener() {
        return listener;
    }

    public void setListener(OnPageClickListener listener) {
        this.listener = listener;
    }

}



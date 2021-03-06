package com.cheersmind.cheersgenie.features.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.util.DensityUtil;

import java.text.SimpleDateFormat;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 测评页面的维度的Recycler适配器
 */
public class ExamDimensionBaseRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public final static int LAYOUT_TYPE_EXAM = 1;//测评
    public final static int LAYOUT_TYPE_TOPIC = 2;//话题
    public final static int LAYOUT_TYPE_DIMENSION = 3;//量表
    public final static int LAYOUT_TYPE_SIMULATE_FOOTER = 4;//模拟footer

    protected Fragment fragment;

    //测评状态
    protected int examStatus;

    //默认Glide处理参数
    protected RequestOptions defaultOptions;

    private RequestOptions blurOptions;

    //标题最大宽度
    int titleMaxWidth;

    SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 初始化默认Glide处理参数
     */
    private void initRequestOptions(Context context) {
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                new CenterCrop(),
                new RoundedCornersTransformation(DensityUtil.dip2px(context, 3), 0, RoundedCornersTransformation.CornerType.ALL));

        MultiTransformation<Bitmap> multiBlur = new MultiTransformation<>(
                new CenterCrop(),
                new BlurTransformation(3),
                new RoundedCornersTransformation(DensityUtil.dip2px(context, 3), 0, RoundedCornersTransformation.CornerType.ALL)
                );

        //默认Glide处理参数
        defaultOptions = new RequestOptions()
                .skipMemoryCache(false)//不忽略内存
                .placeholder(R.drawable.default_image_round)//占位图
//                .placeholder(R.drawable.default_image_round_exam)//占位图
                .error(R.drawable.default_image_round)
//                .fallback(R.drawable.default_image_round) //url为空的时候,显示的图片
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//磁盘缓存策略：缓存所有
                .transform(multi);

        blurOptions = new RequestOptions()
                .skipMemoryCache(false)//不忽略内存
                .placeholder(R.drawable.default_image_round)//占位图
//                .placeholder(R.drawable.default_image_round_exam)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//磁盘缓存策略：缓存所有
                .transform(multiBlur);
    }


    public ExamDimensionBaseRecyclerAdapter(Fragment fragment, List<MultiItemEntity> data) throws QSCustomException {
        super(data);

        this.fragment = fragment;
        if (this.fragment == null) {
            throw new QSCustomException("fragment 不能为空");
        }
        //初始化Glide处理参数
        initRequestOptions(fragment.getContext());
        //获取屏幕宽高
        DisplayMetrics metrics = QSApplication.getMetrics();
        titleMaxWidth = metrics.widthPixels - DensityUtil.dip2px(fragment.getContext(), 60);
    }


    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

    }

    /**
     * 从末尾开始展开指定数量的项
     * @param count 数量
     */
    public void expandAll(int count) {
        int beginPos = mData.size() - 1 + getHeaderLayoutCount();
        int endPos = mData.size() - 1 + getHeaderLayoutCount() - count;
        if (endPos < 0) {
            endPos = getHeaderLayoutCount();
        }
        for (int i = beginPos; i >= endPos; i--) {
            expandAll(i, false, false);
        }
    }


    /**
     * 设置测评状态
     * @param examStatus 状态
     */
    public ExamDimensionBaseRecyclerAdapter setExamStatus(int examStatus) {
        this.examStatus = examStatus;
        return this;
    }


}


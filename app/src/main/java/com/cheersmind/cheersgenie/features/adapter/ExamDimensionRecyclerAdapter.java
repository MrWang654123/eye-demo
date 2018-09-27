package com.cheersmind.cheersgenie.features.adapter;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.RecyclerCommonSection;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.util.DensityUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 测评页面的维度的Recycler适配器
 */
public class ExamDimensionRecyclerAdapter extends BaseSectionQuickAdapter<RecyclerCommonSection<DimensionInfoEntity>, BaseViewHolder> {

    private Fragment fragment;

    //默认Glide处理参数
    private static RequestOptions defaultOptions;

    private static RequestOptions blurOptions;

    //获取屏幕宽高
    DisplayMetrics metrics;
    //标题最大宽度
    int titleMaxWidth;

    /**
     * 初始化默认Glide处理参数
     */
    private void initRequestOptions() {
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                new CenterCrop(),
                new RoundedCornersTransformation(DensityUtil.dip2px(fragment.getActivity(), 12), 0, RoundedCornersTransformation.CornerType.ALL));

        MultiTransformation<Bitmap> multiBlur = new MultiTransformation<>(
                new CenterCrop(),
                new BlurTransformation(12),
                new RoundedCornersTransformation(DensityUtil.dip2px(fragment.getActivity(), 12), 0, RoundedCornersTransformation.CornerType.ALL)
                );

        //默认Glide处理参数
        defaultOptions = new RequestOptions();
        defaultOptions.skipMemoryCache(false);//不忽略内存
        defaultOptions.placeholder(R.drawable.default_image_round);//占位图
        defaultOptions.dontAnimate();//Glide默认是渐变动画，设置dontAnimate()不要动画
        defaultOptions.diskCacheStrategy(DiskCacheStrategy.ALL);//磁盘缓存策略：缓存所有
        defaultOptions.transform(multi);

        blurOptions = new RequestOptions();
        blurOptions.skipMemoryCache(false);//不忽略内存
        blurOptions.placeholder(R.drawable.default_image_round);//占位图
        blurOptions.dontAnimate();//Glide默认是渐变动画，设置dontAnimate()不要动画
        blurOptions.diskCacheStrategy(DiskCacheStrategy.ALL);//磁盘缓存策略：缓存所有
        blurOptions.transform(multiBlur);
    }

    /**
     * 构造方法
     * @param layoutResId 普通项的布局ID
     * @param sectionHeadResId 头部项的布局ID
     * @param data 数据集合
     */
    public ExamDimensionRecyclerAdapter(int layoutResId, int sectionHeadResId, List<RecyclerCommonSection<DimensionInfoEntity>> data) {
        super(layoutResId, sectionHeadResId, data);

        initRequestOptions();
    }

    /**
     * 构造方法
     * @param layoutResId 普通项的布局ID
     * @param sectionHeadResId 头部项的布局ID
     * @param data 数据集合
     */
    public ExamDimensionRecyclerAdapter(Fragment fragment, int layoutResId, int sectionHeadResId, List<RecyclerCommonSection<DimensionInfoEntity>> data) {
        super(layoutResId, sectionHeadResId, data);
        this.fragment = fragment;
        //初始化Glide处理参数
        initRequestOptions();
        //获取屏幕宽高
        metrics = this.fragment.getContext().getResources().getDisplayMetrics();
        titleMaxWidth = metrics.widthPixels - DensityUtil.dip2px(fragment.getContext(), 60);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, RecyclerCommonSection item) {
        //第一个Header的分割线布局隐藏
        int position = helper.getLayoutPosition();
        int headCount = getHeaderLayoutCount();
        boolean isFirst = position - headCount == 0;
        if (isFirst) {
            helper.getView(R.id.tv_divider).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.tv_divider).setVisibility(View.VISIBLE);
        }

//        DimensionInfoEntity dimensionInfo = (DimensionInfoEntity) item.t;
        TopicInfoEntity topicInfo = (TopicInfoEntity) item.getInfo();
        DimensionInfoEntity dimensionInfo = topicInfo.getDimensions().get(0);
        //标题
        helper.setText(R.id.tv_title, item.header);
        //设置标题的最大宽度
        ((TextView)helper.getView(R.id.tv_title)).setMaxWidth(titleMaxWidth);

        //适合人群
        String suitableUser = "";
        //学生
        if (dimensionInfo.getSuitableUser() == Dictionary.Exam_Suitable_User_Student) {
            suitableUser = fragment.getResources().getString(R.string.exam_topic_suitable_user, fragment.getResources().getString(R.string.student));

        } else if (dimensionInfo.getSuitableUser() == Dictionary.Exam_Suitable_User_Parent) {
            //家长
            suitableUser = fragment.getResources().getString(R.string.exam_topic_suitable_user, fragment.getResources().getString(R.string.parent));
        }
        helper.setText(R.id.tv_suitable_user, suitableUser);

        //有效期
        String dateStr = topicInfo.getEndTime();//ISO8601 时间字符串
        SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date date = formatIso8601.parse(dateStr);
            SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy-MM-dd");
            String normalDateStr = formatNormal.format(date);
            helper.setText(R.id.tv_end_date, fragment.getResources().getString(R.string.exam_topic_endtime,normalDateStr));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //查看报告按钮
        TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
        //整个topic已完成
        if (childTopic != null && childTopic.getStatus() == Dictionary.TOPIC_STATUS_COMPLETE) {
            helper.getView(R.id.tv_nav_to_report).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.tv_nav_to_report).setVisibility(View.GONE);
        }

        //场景介绍按钮点击监听
        helper.addOnClickListener(R.id.iv_desc);
        //查看报告按钮点击监听
        helper.addOnClickListener(R.id.tv_nav_to_report);
    }

//    https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=699364419,1344336727&fm=26&gp=0.jpg
    String testImageUrl = "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=699364419,1344336727&fm=26&gp=0.jpg";

    @Override
    protected void convert(BaseViewHolder helper, RecyclerCommonSection item) {
        DimensionInfoEntity dimensionInfo = (DimensionInfoEntity) item.t;
        //标题
        helper.setText(R.id.tv_title2, dimensionInfo.getDimensionName());
        //使用人数
        String useCount = "• " + fragment.getResources().getString(R.string.exam_dimension_use_count, dimensionInfo.getUseCount() + "");
        helper.setText(R.id.tv_used_count, useCount);
        //状态
        DimensionInfoChildEntity childDimension = dimensionInfo.getChildDimension();
        if (childDimension == null) {
            //未开始
            helper.getView(R.id.tv_status).setVisibility(View.GONE);
            ((TextView)helper.getView(R.id.tv_status)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            helper.setText(R.id.tv_status, "未开始");

        } else if (childDimension.getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
            //进行中
            helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
            ((TextView)helper.getView(R.id.tv_status)).setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(fragment.getContext(), R.drawable.doing), null, null, null);
            helper.setText(R.id.tv_status, "进行中");

        } else if (childDimension.getStatus() == Dictionary.DIMENSION_STATUS_COMPLETE) {
            //已完成
            helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
            ((TextView)helper.getView(R.id.tv_status)).setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(fragment.getContext(), R.drawable.complete), null, null, null);
            helper.setText(R.id.tv_status, "已完成");
        }

//        Uri uri = Uri.parse(dimensionInfo.getIcon());
//        //初始化控件
//        SimpleDraweeView draweeView = helper.getView(R.id.iv_icon);
//        //加载图片
//        draweeView.setImageURI(uri);

        //加载图片
        String url = dimensionInfo.getIcon();
//        String url = testImageUrl;
        ImageView imageView = helper.getView(R.id.iv_icon);
        //加载图片，区分是否被锁
        if (dimensionInfo.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
            //被锁情况
            if (!url.equals(imageView.getTag(R.id.iv_icon))) {
                Glide.with(fragment)
                        .load(R.drawable.default_image_round)
//                    .load(url)
//                        .thumbnail(0.5f)
                        .apply(blurOptions)
                        .into(imageView);
                imageView.setTag(R.id.iv_icon, url);
            }

        } else {
            //未被锁：正常显示图片
            if (!TextUtils.isEmpty(url) && !url.equals(imageView.getTag(R.id.iv_icon))) {
                Glide.with(fragment)
                        .load(R.drawable.default_image_round)
//                    .load(url)
//                        .thumbnail(0.5f)
                        .apply(defaultOptions)
                        .into(imageView);
                imageView.setTag(R.id.iv_icon, url);
            }
        }

        //是否被锁，显隐锁图标
        if (dimensionInfo.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
            (helper.getView(R.id.iv_lock)).setVisibility(View.VISIBLE);
        } else {
            (helper.getView(R.id.iv_lock)).setVisibility(View.GONE);
        }

    }

}

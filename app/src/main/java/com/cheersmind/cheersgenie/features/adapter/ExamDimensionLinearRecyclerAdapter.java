package com.cheersmind.cheersgenie.features.adapter;

import android.content.Context;
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
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.RecyclerCommonSection;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
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
public class ExamDimensionLinearRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private Fragment fragment;

    //默认Glide处理参数
    private static RequestOptions defaultOptions;

    private static RequestOptions blurOptions;

    //标题最大宽度
    private int titleMaxWidth;

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

    public ExamDimensionLinearRecyclerAdapter(Fragment fragment, List<MultiItemEntity> data) throws QSCustomException {
        super(data);
        addItemType(0, R.layout.recycleritem_axam_header);
        addItemType(1, R.layout.recycleritem_axam_linear);

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
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (item.getItemType()) {
            //header（话题）
            case 0: {
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
                final TopicInfoEntity topicInfo = (TopicInfoEntity) item;
                DimensionInfoEntity dimensionInfo = topicInfo.getDimensions().get(0);
                //标题
                helper.setText(R.id.tv_title, topicInfo.getTopicName());
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

                //伸缩按钮
                helper.setImageResource(R.id.iv_expand, topicInfo.isExpanded() ? R.drawable.ic_arrow_drop_up_black_24dp : R.drawable.ic_arrow_drop_down_black_24dp);
                helper.getView(R.id.iv_expand).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
                        if (topicInfo.isExpanded()) {
                            collapse(pos);
                        } else {
//                            if (pos % 3 == 0) {
//                                expandAll(pos, false);
//                            } else {
                            expand(pos);
//                            }
                        }
                    }
                });

                break;
            }
            //body（量表）
            case 1: {
                DimensionInfoEntity dimensionInfo = (DimensionInfoEntity) item;
                //标题
                helper.setText(R.id.tv_title2, dimensionInfo.getDimensionName());
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

                //是否被锁，显隐锁图标
                if (dimensionInfo.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
                    (helper.getView(R.id.iv_lock)).setVisibility(View.VISIBLE);
                } else {
                    (helper.getView(R.id.iv_lock)).setVisibility(View.GONE);
                }
                break;
            }
        }
    }

}

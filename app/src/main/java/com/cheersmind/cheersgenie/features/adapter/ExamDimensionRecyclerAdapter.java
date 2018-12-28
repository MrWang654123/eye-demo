package com.cheersmind.cheersgenie.features.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.view.CircleProgressBar;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ExamEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 测评页面的维度的Recycler适配器
 */
public class ExamDimensionRecyclerAdapter extends ExamDimensionBaseRecyclerAdapter {

    public ExamDimensionRecyclerAdapter(Fragment fragment, List<MultiItemEntity> data) throws QSCustomException {
        super(fragment, data);

        addItemType(LAYOUT_TYPE_EXAM, R.layout.recycleritem_axam_title);
        addItemType(LAYOUT_TYPE_TOPIC, R.layout.recycleritem_axam_header);
        addItemType(LAYOUT_TYPE_DIMENSION, R.layout.recycleritem_axam_item);
    }


    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            //测评
            case LAYOUT_TYPE_EXAM: {
//                //第一个测评隐藏顶部外间距布局
//                int position = helper.getLayoutPosition();
//                int headCount = getHeaderLayoutCount();
//                boolean isFirst = position - headCount == 0;
//                if (isFirst) {
//                    helper.getView(R.id.tv_divider).setVisibility(View.GONE);
//                } else {
//                    helper.getView(R.id.tv_divider).setVisibility(View.VISIBLE);
//                }

                ExamEntity exam = (ExamEntity) item;
                //第一个测评隐藏顶部外间距布局
                if (((ExamEntity) item).isFirstInSeminar()) {
                    helper.getView(R.id.tv_divider).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.tv_divider).setVisibility(View.VISIBLE);
                }
                //测评名称
                if (!TextUtils.isEmpty(exam.getExamName())) {
                    helper.getView(R.id.tv_title).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_title, exam.getExamName());
                } else {
                    helper.getView(R.id.tv_title).setVisibility(View.GONE);
                }
                //有效期
                if (!TextUtils.isEmpty(exam.getStartTime())  && !TextUtils.isEmpty(exam.getEndTime())) {
                    //有效期
                    String startTime = exam.getStartTime();
                    String endTime = exam.getEndTime();//ISO8601 时间字符串
                    try {
                        Date startDate = formatIso8601.parse(startTime);
                        Date endDate = formatIso8601.parse(endTime);
                        String startDateStr = formatNormal.format(startDate);
                        String endDateStr = formatNormal.format(endDate);
                        helper.setText(R.id.tv_date, fragment.getResources().getString(R.string.exam_date,startDateStr, endDateStr));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                //圆形进度条
                try {
                    float progress = (float)exam.getCompleteDimensions() / exam.getTotalDimensions() * 100;
                    ((CircleProgressBar) helper.getView(R.id.circleProgressBar)).setProgress((int) progress);
//                circleProgressBar.setProgress(progress, true); // 使用数字过渡动画
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //把测评对象设置到子项视图的tag中
                helper.itemView.setTag(exam);

                break;
            }
            //subtitle（话题）
            case LAYOUT_TYPE_TOPIC: {
//                //第一个Header的分割线布局隐藏
//                int position = helper.getLayoutPosition();
//                int headCount = getHeaderLayoutCount();
//                boolean isFirst = position - headCount == 0;
//                if (isFirst) {
//                    helper.getView(R.id.tv_divider).setVisibility(View.GONE);
//                } else {
//                    helper.getView(R.id.tv_divider).setVisibility(View.VISIBLE);
//                }

                final TopicInfoEntity topicInfo = (TopicInfoEntity) item;
//                DimensionInfoEntity dimensionInfo = topicInfo.getDimensions().get(0);

                //第一个Header的分割线布局隐藏
//                boolean isFirst = topicInfo.isFirstInExam();
//                if (isFirst) {
//                    helper.getView(R.id.tv_divider).setVisibility(View.GONE);
//                } else {
//                    helper.getView(R.id.tv_divider).setVisibility(View.VISIBLE);
//                }

                //标题
                helper.setText(R.id.tv_title, topicInfo.getTopicName());
                //状态角标
                TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
//                //整个topic已完成
                if (childTopic != null && childTopic.getStatus() == Dictionary.TOPIC_STATUS_COMPLETE) {
                    helper.getView(R.id.iv_status).setVisibility(View.VISIBLE);
                    helper.getView(R.id.iv_right).setVisibility(View.INVISIBLE);
                } else {
                    helper.getView(R.id.iv_status).setVisibility(View.GONE);
                    helper.getView(R.id.iv_right).setVisibility(View.VISIBLE);
                }

                //设置标题的最大宽度
//                ((TextView)helper.getView(R.id.tv_title)).setMaxWidth(titleMaxWidth);

                //适合人群
//                String suitableUser = "";
//                //学生
//                if (dimensionInfo.getSuitableUser() == Dictionary.Exam_Suitable_User_Student) {
//                    suitableUser = fragment.getResources().getString(R.string.exam_topic_suitable_user, fragment.getResources().getString(R.string.student));
//
//                } else if (dimensionInfo.getSuitableUser() == Dictionary.Exam_Suitable_User_Parent) {
//                    //家长
//                    suitableUser = fragment.getResources().getString(R.string.exam_topic_suitable_user, fragment.getResources().getString(R.string.parent));
//                }
//                helper.setText(R.id.tv_suitable_user, suitableUser);

                //有效期
//                String dateStr = topicInfo.getEndTime();//ISO8601 时间字符串
//                try {
//                    Date date = formatIso8601.parse(dateStr);
//                    String normalDateStr = formatNormal.format(date);
//                    helper.setText(R.id.tv_end_date, fragment.getResources().getString(R.string.exam_topic_endtime,normalDateStr));
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                //查看报告按钮
//                TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
//                //整个topic已完成
//                if (childTopic != null && childTopic.getStatus() == Dictionary.TOPIC_STATUS_COMPLETE) {
//                    helper.getView(R.id.tv_nav_to_report).setVisibility(View.VISIBLE);
//                } else {
//                    helper.getView(R.id.tv_nav_to_report).setVisibility(View.GONE);
//                }
//
                //场景介绍按钮点击监听
//                helper.addOnClickListener(R.id.iv_desc);
//                //查看报告按钮点击监听
//                helper.addOnClickListener(R.id.tv_nav_to_report);
//
//                //伸缩按钮（网格模式隐藏）
//                helper.getView(R.id.iv_expand).setVisibility(View.GONE);
//                helper.setImageResource(R.id.iv_expand, topicInfo.isExpanded() ? R.drawable.ic_arrow_drop_up_black_24dp : R.drawable.ic_arrow_drop_down_black_24dp);
//                //伸缩按钮监听
//                helper.addOnClickListener(R.id.iv_expand);
////                helper.getView(R.id.iv_expand).setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        int pos = helper.getAdapterPosition();
////                        if (topicInfo.isExpanded()) {
////                            collapse(pos);
////                        } else {
////                            expand(pos);
////                        }
////                    }
////                });

                break;
            }

            //body（量表）
            case LAYOUT_TYPE_DIMENSION: {
                DimensionInfoEntity dimensionInfo = (DimensionInfoEntity) item;

                //最后一个量表
                if (dimensionInfo.isLastInTopic()) {
                    helper.getView(R.id.cl_content).setBackgroundResource(R.drawable.exam_item_footer);
                    helper.getView(R.id.tv_footer_padding).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.cl_content).setBackgroundResource(R.drawable.exam_item_body);
                    helper.getView(R.id.tv_footer_padding).setVisibility(View.GONE);
                }

                //标题
                helper.setText(R.id.tv_title2, dimensionInfo.getDimensionName());

                //使用人数（0隐藏）
                if (dimensionInfo.getUseCount() > 0) {
                    String useCount = "• " + fragment.getResources().getString(R.string.exam_dimension_use_count, dimensionInfo.getUseCount() + "");
                    helper.getView(R.id.tv_used_count).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_used_count, useCount);
                } else {
                    helper.getView(R.id.tv_used_count).setVisibility(View.GONE);
                }

                //描述
                if (!TextUtils.isEmpty(dimensionInfo.getDescription())) {
                    helper.getView(R.id.tv_desc).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_desc, dimensionInfo.getDescription());
                } else {
                    //被锁
                    if (dimensionInfo.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
                        helper.getView(R.id.tv_desc).setVisibility(View.INVISIBLE);
                    } else {
                        helper.getView(R.id.tv_desc).setVisibility(View.GONE);
                    }
                }

                //状态
                DimensionInfoChildEntity childDimension = dimensionInfo.getChildDimension();
                if (childDimension == null) {
                    //未开始
                    helper.getView(R.id.tv_status).setVisibility(View.GONE);
                    ((TextView)helper.getView(R.id.tv_status)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    helper.setText(R.id.tv_status, "未开始");

                } else if (childDimension.getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                    //测评已结束则不显示进行中的状态
                    if (examStatus == Dictionary.EXAM_STATUS_OVER) {
                        helper.getView(R.id.tv_status).setVisibility(View.GONE);
                    } else {
                        //进行中
                        helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
                        ((TextView) helper.getView(R.id.tv_status)).setCompoundDrawablesWithIntrinsicBounds(
                                ContextCompat.getDrawable(fragment.getContext(), R.drawable.doing), null, null, null);
                        helper.setText(R.id.tv_status, "进行中");
                    }

                } else if (childDimension.getStatus() == Dictionary.DIMENSION_STATUS_COMPLETE) {
                    //已完成
                    helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
                    ((TextView)helper.getView(R.id.tv_status)).setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(fragment.getContext(), R.drawable.complete), null, null, null);
                    helper.setText(R.id.tv_status, "已完成");
                }

                //加载图片
//                String url = dimensionInfo.getIcon();
////        String url = testImageUrl;
//                ImageView imageView = helper.getView(R.id.iv_icon);
//                //加载图片，区分是否被锁
//                if (dimensionInfo.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
//                    //被锁情况
//                    if (!TextUtils.isEmpty(url)) {
//                        if (!url.equals(imageView.getTag(R.id.iv_icon))) {
//                            Glide.with(fragment)
////                        .load(R.drawable.default_image_round)
//                                    .load(url)
////                        .thumbnail(0.5f)
//                                    .apply(defaultOptions)
//                                    .into(imageView);
//                            imageView.setTag(R.id.iv_icon, url);
//                        }
//                    } else {
//                        Glide.with(fragment)
//                                .load(R.drawable.default_image_round)
////                                .load(url)
////                        .thumbnail(0.5f)
//                                .apply(defaultOptions)
//                                .into(imageView);
//                        imageView.setTag(R.id.iv_icon, url);
//                    }
//
//                } else {
//                    //未被锁：正常显示图片
//                    if (!TextUtils.isEmpty(url)) {
//                        if (!url.equals(imageView.getTag(R.id.iv_icon))) {
//                            Glide.with(fragment)
////                        .load(R.drawable.default_image_round)
//                                    .load(url)
////                        .thumbnail(0.5f)
//                                    .apply(defaultOptions)
//                                    .into(imageView);
//                            imageView.setTag(R.id.iv_icon, url);
//                        }
//                    } else {
//                        Glide.with(fragment)
//                        .load(R.drawable.default_image_round)
////                                .load(url)
////                        .thumbnail(0.5f)
//                                .apply(defaultOptions)
//                                .into(imageView);
//                        imageView.setTag(R.id.iv_icon, url);
//                    }
//                }

                //加载图片
                SimpleDraweeView imageView = helper.getView(R.id.iv_icon);
//                if (!TextUtils.isEmpty(dimensionInfo.getIcon())) {
//                    imageView.setImageURI(dimensionInfo.getIcon());
//                } else {
//                    imageView.setActualImageResource(R.drawable.default_image_round);
//                }
                imageView.setImageURI(dimensionInfo.getIcon());

                //是否被锁，显隐锁图标
                if (dimensionInfo.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
                    (helper.getView(R.id.iv_lock)).setVisibility(View.VISIBLE);
                } else {
                    (helper.getView(R.id.iv_lock)).setVisibility(View.GONE);
                }

//                //最后一个量表
//                if (dimensionInfo.isLastInTopic()) {
//                    //把最后一个量表对象设置到子项视图的tag中
//                    helper.itemView.setTag(dimensionInfo);
//                }

                //把量表对象设置到子项视图的tag中
                helper.itemView.setTag(dimensionInfo);

                break;
            }
        }

    }

}


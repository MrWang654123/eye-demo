package com.cheersmind.cheersgenie.features.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

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

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 测评页面的维度的Recycler适配器
 */
public class ExamDimensionLinearRecyclerAdapter extends ExamDimensionBaseRecyclerAdapter {


    public ExamDimensionLinearRecyclerAdapter(Fragment fragment, List<MultiItemEntity> data) throws QSCustomException {
        super(fragment, data);

        addItemType(LAYOUT_TYPE_EXAM, R.layout.recycleritem_axam_title_empty);
        addItemType(LAYOUT_TYPE_TOPIC, R.layout.recycleritem_axam_header);
        addItemType(LAYOUT_TYPE_DIMENSION, R.layout.recycleritem_axam_linear);
    }


    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            //测评
            case LAYOUT_TYPE_EXAM: {
//                ExamEntity exam = (ExamEntity) item;
//                //测评名称
//                if (!TextUtils.isEmpty(exam.getExamName())) {
//                    helper.getView(R.id.tv_title).setVisibility(View.VISIBLE);
//                    helper.setText(R.id.tv_title, exam.getExamName());
//                } else {
//                    helper.getView(R.id.tv_title).setVisibility(View.GONE);
//                }
//                //有效期
//                if (!TextUtils.isEmpty(exam.getStartTime())  && !TextUtils.isEmpty(exam.getEndTime())) {
//                    //有效期
//                    String startTime = exam.getStartTime();
//                    String endTime = exam.getEndTime();//ISO8601 时间字符串
//                    try {
//                        Date startDate = formatIso8601.parse(startTime);
//                        Date endDate = formatIso8601.parse(endTime);
//                        String startDateStr = formatNormal.format(startDate);
//                        String endDateStr = formatNormal.format(endDate);
//                        helper.setText(R.id.tv_date, fragment.getResources().getString(R.string.exam_date,startDateStr, endDateStr));
//
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                //圆形进度条
//                try {
//                    float progress = (float)exam.getCompleteDimensions() / exam.getTotalDimensions() * 100;
//                    ((CircleProgressBar) helper.getView(R.id.circleProgressBar)).setProgress((int) progress);
////                circleProgressBar.setProgress(progress, true); // 使用数字过渡动画
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                break;
            }
            //header（话题）
            case LAYOUT_TYPE_TOPIC: {
                //第一个Header的分割线布局隐藏
//                int position = helper.getLayoutPosition();
//                int headCount = getHeaderLayoutCount();
//                boolean isFirst = position - headCount == 0;
//                if (isFirst) {
//                    helper.getView(R.id.tv_divider).setVisibility(View.GONE);
//                } else {
//                    helper.getView(R.id.tv_divider).setVisibility(View.VISIBLE);
//                }

                final TopicInfoEntity topicInfo = (TopicInfoEntity) item;
                DimensionInfoEntity dimensionInfo = topicInfo.getDimensions().get(0);

                //第一个Header的分割线布局隐藏
                boolean isFirst = topicInfo.isFirstInExam();
                if (isFirst) {
                    helper.getView(R.id.tv_divider).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.tv_divider).setVisibility(View.VISIBLE);
                }

                //标题
                helper.setText(R.id.tv_title, topicInfo.getTopicName());
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
                helper.addOnClickListener(R.id.iv_desc);
//                //查看报告按钮点击监听
//                helper.addOnClickListener(R.id.tv_nav_to_report);
//
//                //伸缩按钮
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
//////                            if (pos % 3 == 0) {
//////                                expandAll(pos, false);
//////                            } else {
////                            expand(pos);
//////                            }
////                        }
////                    }
////                });

                break;
            }
            //body（量表）
            case LAYOUT_TYPE_DIMENSION: {
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

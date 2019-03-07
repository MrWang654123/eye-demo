package com.cheersmind.cheersgenie.features.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.utils.DateTimeUtils;
import com.cheersmind.cheersgenie.features.entity.ExamEntity;
import com.cheersmind.cheersgenie.features.entity.SeminarEntity;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 历史专题recycler适配器
 */
public class HistorySeminarRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public final static int LAYOUT_TYPE_HEADER = 1;//header
    public final static int LAYOUT_TYPE_ITEM = 2;//body项

    private SimpleDateFormat formatIso8601;
    private SimpleDateFormat formatNormal;
    //日期格式
    private final static String DATETIME_FORMAT = "yyyy年 MM月 dd日";////hh:mm a, dd-MMM-yyyy


    public HistorySeminarRecyclerAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(LAYOUT_TYPE_HEADER, R.layout.recycleritem_seminar_header);
        addItemType(LAYOUT_TYPE_ITEM, R.layout.recycleritem_seminar_item);
    }


    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            //header
            case LAYOUT_TYPE_HEADER: {
                SeminarEntity seminar = (SeminarEntity) item;
                //专题名称
                if (!TextUtils.isEmpty(seminar.getSeminar_name())) {
                    helper.getView(R.id.tv_title).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_title, seminar.getSeminar_name());
                } else {
                    helper.getView(R.id.tv_title).setVisibility(View.INVISIBLE);
                }
                break;
            }
            //body项
            case LAYOUT_TYPE_ITEM: {
                ExamEntity exam = (ExamEntity) item;

//                int position = helper.getLayoutPosition();
//                int headCount = getHeaderLayoutCount();
//                boolean isFirst = position - headCount == 0;
//                boolean isLast = position - headCount == getItemCount() - 1;
                //轴线
                //第一个
                if (exam.isFirstInSeminar()) {
                    //轴线
                    helper.getView(R.id.tv_timeline_up).setVisibility(View.INVISIBLE);
                } else {
                    helper.getView(R.id.tv_timeline_up).setVisibility(View.VISIBLE);
                }
                //最后一个
                if (exam.isLastInSeminar()) {
                    //轴线
                    helper.getView(R.id.tv_timeline_down).setVisibility(View.INVISIBLE);
                    //背景
                    helper.getView(R.id.ll_content).setBackgroundResource(R.drawable.exam_item_footer);
                } else {
                    helper.getView(R.id.tv_timeline_down).setVisibility(View.VISIBLE);
                    helper.getView(R.id.ll_content).setBackgroundResource(R.drawable.exam_item_body);
                }

                AnimatorSet animatorSet = (AnimatorSet) helper.getView(R.id.iv_status).getTag();
                if (animatorSet == null) {
                    animatorSet = new AnimatorSet();//组合动画
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(helper.getView(R.id.iv_status), "scaleX", 0.6f, 1.1f);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(helper.getView(R.id.iv_status), "scaleY", 0.6f, 1.1f);
                    scaleX.setRepeatCount(ValueAnimator.INFINITE);
                    scaleX.setRepeatMode(ValueAnimator.REVERSE);
                    scaleY.setRepeatCount(ValueAnimator.INFINITE);
                    scaleY.setRepeatMode(ValueAnimator.REVERSE);
                    animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                    animatorSet.setDuration(500);
                    animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
//            animatorSet.start();

                    helper.getView(R.id.iv_status).setTag(animatorSet);
                }

                //测评状态
                int examStatus = exam.getStatus();
                if(examStatus == Dictionary.EXAM_STATUS_OVER) {
                    helper.setImageResource(R.id.iv_status, R.drawable.task_over);
                    animatorSet.cancel();

                } else if(examStatus == Dictionary.EXAM_STATUS_DOING) {
                    helper.setImageResource(R.id.iv_status, R.drawable.task_doing);
                    animatorSet.start();

                } else {
                    helper.setImageResource(R.id.iv_status, R.drawable.task_inactive);
                    animatorSet.cancel();
                }

                //孩子测评状态
                int childExamStatus = exam.getChildExamStatus();
                helper.getView(R.id.iv_badge).setVisibility(View.VISIBLE);
                if(childExamStatus == Dictionary.CHILD_EXAM_STATUS_COMPLETE) {
                    helper.setImageResource(R.id.iv_badge, R.drawable.exam_status_complete);

                } else {
                    //非未开始的才显示未完成状态
                    if (examStatus != Dictionary.EXAM_STATUS_INACTIVE) {
                        helper.setImageResource(R.id.iv_badge, R.drawable.exam_status_no_complete);
                    } else {
                        helper.getView(R.id.iv_badge).setVisibility(View.GONE);
                    }
                }

                //开始时间
                if(!TextUtils.isEmpty(exam.getStartTime())) {
                    helper.getView(R.id.ll_begin_time).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_begin_time, DateTimeUtils.parseDateTime(exam.getStartTime(),
                            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                            DATETIME_FORMAT));
                }
                else
                    helper.getView(R.id.ll_begin_time).setVisibility(View.GONE);

                //结束时间
                if(!TextUtils.isEmpty(exam.getEndTime())) {
                    helper.getView(R.id.ll_end_time).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_end_time, DateTimeUtils.parseDateTime(exam.getEndTime(),
                            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                            DATETIME_FORMAT));
                }
                else
                    helper.getView(R.id.ll_end_time).setVisibility(View.GONE);

                //测评名称
                if (!TextUtils.isEmpty(exam.getExamName())) {
                    helper.setText(R.id.tv_name, exam.getExamName());
                } else {
                    helper.setText(R.id.tv_name, "--");
                }

                break;
            }
        }
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

}

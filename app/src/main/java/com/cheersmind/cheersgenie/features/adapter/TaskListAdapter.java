package com.cheersmind.cheersgenie.features.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.utils.DateTimeUtils;
import com.cheersmind.cheersgenie.main.entity.ExamEntity;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

/**
 * 任务列表适配器
 */
public class TaskListAdapter extends BaseQuickAdapter<ExamEntity, BaseViewHolder> {
    //日期格式
    private final static String DATETIME_FORMAT = "yyyy年 MM月 dd日";////hh:mm a, dd-MMM-yyyy


    public TaskListAdapter(int layoutResId, List<ExamEntity> dataList) {
        super(layoutResId, dataList);
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    protected void convert(BaseViewHolder helper, ExamEntity item) {
        int position = helper.getLayoutPosition();
        int headCount = getHeaderLayoutCount();
        boolean isFirst = position - headCount == 0;
        boolean isLast = position - headCount == getItemCount() - 1;
        //轴线
        if (isFirst) {
            helper.getView(R.id.tv_timeline_up).setVisibility(View.INVISIBLE);
        } else {
            helper.getView(R.id.tv_timeline_up).setVisibility(View.VISIBLE);
        }
        if (isLast) {
            helper.getView(R.id.tv_timeline_down).setVisibility(View.INVISIBLE);
        } else {
            helper.getView(R.id.tv_timeline_down).setVisibility(View.VISIBLE);
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

        //孩子测评状态
        int childExamStatus = item.getChildExamStatus();
        if(childExamStatus == Dictionary.CHILD_EXAM_STATUS_COMPLETE) {
            helper.setImageResource(R.id.iv_badge, R.drawable.exam_status_complete);

        } else {
            helper.setImageResource(R.id.iv_badge, R.drawable.exam_status_no_complete);
        }

        //测评状态
        int examStatus = item.getStatus();
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

        //开始时间
        if(!TextUtils.isEmpty(item.getStartTime())) {
            helper.getView(R.id.ll_begin_time).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_begin_time,DateTimeUtils.parseDateTime(item.getStartTime(),
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                    DATETIME_FORMAT));
        }
        else
            helper.getView(R.id.ll_begin_time).setVisibility(View.GONE);

        //结束时间
        if(!TextUtils.isEmpty(item.getEndTime())) {
            helper.getView(R.id.ll_end_time).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_end_time, DateTimeUtils.parseDateTime(item.getEndTime(),
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                    DATETIME_FORMAT));
        }
        else
            helper.getView(R.id.ll_end_time).setVisibility(View.GONE);

        //测评名称
        if (!TextUtils.isEmpty(item.getExamName())) {
            helper.setText(R.id.tv_name, item.getExamName());
        } else {
            helper.setText(R.id.tv_name, "--");
        }
    }

}

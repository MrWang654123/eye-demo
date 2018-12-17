package com.cheersmind.cheersgenie.features.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.holder.TimeLineViewHolder;
import com.cheersmind.cheersgenie.features.utils.DateTimeUtils;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.main.entity.TaskItemEntity;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

/**
 * 任务列表适配器
 */
public class TaskListAdapter extends BaseQuickAdapter<TaskItemEntity, BaseViewHolder> {
    //日期格式
    private final static String DATETIME_FORMAT = "yyyy-MM-dd";////hh:mm a, dd-MMM-yyyy


    public TaskListAdapter(int layoutResId, List<TaskItemEntity> feedList) {
        super(layoutResId, feedList);
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskItemEntity item) {
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
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(helper.getView(R.id.iv_status), "scaleX", 1f, 1.3f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(helper.getView(R.id.iv_status), "scaleY", 1f, 1.3f);
            scaleX.setRepeatCount(ValueAnimator.INFINITE);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
//            animatorSet.start();

            helper.getView(R.id.iv_status).setTag(animatorSet);
        }

        //状态
        int status = item.getStatus();
        if(status == Dictionary.TASK_STATUS_COMPLETE) {
            helper.setImageResource(R.id.iv_status, R.drawable.task_complete);
            animatorSet.cancel();
            helper.getView(R.id.iv_badge).setVisibility(View.GONE);

        } else if(status == Dictionary.TASK_STATUS_DOING) {
            helper.setImageResource(R.id.iv_status, R.drawable.task_doing);
            animatorSet.start();
            helper.getView(R.id.iv_badge).setVisibility(View.VISIBLE);

        } else {
            helper.setImageResource(R.id.iv_status, R.drawable.task_inactive);
            animatorSet.cancel();
            helper.getView(R.id.iv_badge).setVisibility(View.GONE);
        }

        //开始时间
        if(!TextUtils.isEmpty(item.getStart_time())) {
            helper.getView(R.id.ll_begin_time).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_begin_time,DateTimeUtils.parseDateTime(item.getStart_time(),
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                    DATETIME_FORMAT));
        }
        else
            helper.getView(R.id.ll_begin_time).setVisibility(View.GONE);

        //结束时间
        if(!TextUtils.isEmpty(item.getEnd_time())) {
            helper.getView(R.id.ll_end_time).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_end_time, DateTimeUtils.parseDateTime(item.getEnd_time(),
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                    DATETIME_FORMAT));
        }
        else
            helper.getView(R.id.ll_end_time).setVisibility(View.GONE);

        //描述信息
        if (!TextUtils.isEmpty(item.getExam_name())) {
            helper.setText(R.id.tv_name, item.getExam_name());
        } else {
            helper.setText(R.id.tv_name, "--");
        }
    }

}

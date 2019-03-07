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

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.holder.TimeLineViewHolder;
import com.cheersmind.cheersgenie.features.utils.DateTimeUtils;
import com.cheersmind.cheersgenie.features.entity.TaskItemEntity;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

/**
 * 时间轴适配器
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {
    //日期格式
    private final static String DATETIME_FORMAT = "yyyy-MM-dd";////hh:mm a, dd-MMM-yyyy

    private List<TaskItemEntity> mFeedList;
    private Context mContext;
    private boolean mWithLinePadding;

    //正在进行中状态的动画
    private Animation mStatusDoingAnimation;

    public TimeLineAdapter(Context context, List<TaskItemEntity> feedList, boolean withLinePadding) {
        mFeedList = feedList;
        mWithLinePadding = withLinePadding;
//        mStatusDoingAnimation = AnimationUtils.loadAnimation(context, R.anim.task_list_status_doing);
        mStatusDoingAnimation = new ScaleAnimation(1, 1.3f, 1, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mStatusDoingAnimation.setDuration(400);
        mStatusDoingAnimation.setRepeatCount(Animation.INFINITE);
        mStatusDoingAnimation.setRepeatMode(Animation.REVERSE);
        mStatusDoingAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                System.out.println("onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                System.out.println("onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                System.out.println("onAnimationRepeat");
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        //是否有间距
        View view = mLayoutInflater.inflate(R.layout.item_timeline_line_padding_custom, parent, false);

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final TimeLineViewHolder holder, int position) {
        TaskItemEntity timeLineModel = mFeedList.get(position);

//        if(timeLineModel.getStatus() == Dictionary.EXAM_STATUS_OVER) {
//            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
//        } else if(timeLineModel.getStatus() == Dictionary.EXAM_STATUS_DOING) {
//            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorAccent));
//        } else {
//            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorAccent));
//        }

        //轴线
        if (position == 0) {
            holder.tv_timeline_up.setVisibility(View.INVISIBLE);
        } else {
            holder.tv_timeline_up.setVisibility(View.VISIBLE);
        }
        if (position == getItemCount() - 1) {
            holder.tv_timeline_down.setVisibility(View.INVISIBLE);
        } else {
            holder.tv_timeline_down.setVisibility(View.VISIBLE);
        }

        AnimatorSet animatorSet = (AnimatorSet) holder.ivStatus.getTag();
        if (animatorSet == null) {
            animatorSet = new AnimatorSet();//组合动画
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.ivStatus, "scaleX", 1f, 1.3f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.ivStatus, "scaleY", 1f, 1.3f);
            scaleX.setRepeatCount(ValueAnimator.INFINITE);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
//            animatorSet.start();

            holder.ivStatus.setTag(animatorSet);
        }

        //状态
        int status = timeLineModel.getStatus();
        if(status == Dictionary.EXAM_STATUS_OVER) {
            holder.ivStatus.setImageResource(R.drawable.task_over);
//            holder.ivStatus.clearAnimation();
            animatorSet.cancel();
            holder.iv_badge.setVisibility(View.GONE);

        } else if(status == Dictionary.EXAM_STATUS_DOING) {
            holder.ivStatus.setImageResource(R.drawable.task_doing);
            animatorSet.start();
            //视图动画
//            holder.ivStatus.clearAnimation();
//            holder.ivStatus.startAnimation(mStatusDoingAnimation);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    holder.ivStatus.postInvalidateOnAnimation();
//                }
//            }).start();

            holder.iv_badge.setVisibility(View.VISIBLE);

        } else {
            holder.ivStatus.setImageResource(R.drawable.task_inactive);
//            holder.ivStatus.clearAnimation();
            animatorSet.cancel();
            holder.iv_badge.setVisibility(View.GONE);
        }

        //开始时间
        if(!TextUtils.isEmpty(timeLineModel.getStart_time())) {
            holder.ll_begin_time.setVisibility(View.VISIBLE);
            holder.tvBeginTime.setText(DateTimeUtils.parseDateTime(timeLineModel.getStart_time(),
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                    DATETIME_FORMAT));
        }
        else
            holder.ll_begin_time.setVisibility(View.GONE);

        //结束时间
        if(!TextUtils.isEmpty(timeLineModel.getEnd_time())) {
            holder.ll_end_time.setVisibility(View.VISIBLE);
            holder.tvEndTime.setText(DateTimeUtils.parseDateTime(timeLineModel.getEnd_time(),
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                    DATETIME_FORMAT));
        }
        else
            holder.ll_end_time.setVisibility(View.GONE);

        //描述信息
        if (!TextUtils.isEmpty(timeLineModel.getExam_name())) {
            holder.tv_name.setText(timeLineModel.getExam_name());
        } else {
            holder.tv_name.setText("--");
//            holder.tvDesc.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

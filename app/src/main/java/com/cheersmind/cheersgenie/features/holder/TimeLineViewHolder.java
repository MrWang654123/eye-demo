package com.cheersmind.cheersgenie.features.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.github.vipulasri.timelineview.TimelineView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 时间轴列表的ViewHolder
 */
public class TimeLineViewHolder extends RecyclerView.ViewHolder {
    //上边的线
    @BindView(R.id.tv_timeline_up)
    public TextView tv_timeline_up;
    //状态图标
    @BindView(R.id.iv_status)
    public ImageView ivStatus;
    //下边的线
    @BindView(R.id.tv_timeline_down)
    public TextView tv_timeline_down;
    //进行中的图标
    @BindView(R.id.iv_badge)
    public ImageView iv_badge;
    //名称
    @BindView(R.id.tv_name)
    public TextView tv_name;
    //开始时间容器
    @BindView(R.id.ll_begin_time)
    public LinearLayout ll_begin_time;
    //开始时间
    @BindView(R.id.tv_begin_time)
    public TextView tvBeginTime;
    //结束时间容器
    @BindView(R.id.ll_end_time)
    public LinearLayout ll_end_time;
    //结束时间
    @BindView(R.id.tv_end_time)
    public TextView tvEndTime;
//    @BindView(R.id.time_marker)
//    public TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);

        ButterKnife.bind(this, itemView);
//        mTimelineView.initLine(viewType);
    }
}

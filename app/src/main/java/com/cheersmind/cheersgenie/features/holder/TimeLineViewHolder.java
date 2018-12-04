package com.cheersmind.cheersgenie.features.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.github.vipulasri.timelineview.TimelineView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 时间轴列表的ViewHolder
 */
public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_begin_time)
    public TextView tvBeginTime;
    @BindView(R.id.tv_end_time)
    public TextView tvEndTime;
    @BindView(R.id.tv_desc)
    public TextView tvDesc;
    @BindView(R.id.time_marker)
    public TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        mTimelineView.initLine(viewType);
    }
}

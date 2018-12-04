package com.cheersmind.cheersgenie.features.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.holder.TimeLineViewHolder;
import com.cheersmind.cheersgenie.features.utils.DateTimeUtils;
import com.cheersmind.cheersgenie.features.utils.VectorDrawableUtils;
import com.cheersmind.cheersgenie.main.entity.TaskItemEntity;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

/**
 * 时间轴适配器
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TaskItemEntity> mFeedList;
    private Context mContext;
    private boolean mWithLinePadding;
    //日期格式
    private final static String DATETIME_FORMAT = "yyyy-MM月-dd日 HH:mm";

    public TimeLineAdapter(List<TaskItemEntity> feedList, boolean withLinePadding) {
        mFeedList = feedList;
        mWithLinePadding = withLinePadding;
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
        View view = mLayoutInflater.inflate(mWithLinePadding ? R.layout.item_timeline_line_padding : R.layout.item_timeline, parent, false);

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineViewHolder holder, int position) {

        TaskItemEntity timeLineModel = mFeedList.get(position);

        if(timeLineModel.getStatus() == Dictionary.TASK_STATUS_COMPLETE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if(timeLineModel.getStatus() == Dictionary.TASK_STATUS_DOING) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorAccent));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorAccent));
        }

        //开始时间
        if(!TextUtils.isEmpty(timeLineModel.getStart_time())) {
            holder.tvBeginTime.setVisibility(View.VISIBLE);
            //hh:mm a, dd-MMM-yyyy
            holder.tvBeginTime.setText(mContext.getResources().getString(R.string.begin_time_fmt,
                    DateTimeUtils.parseDateTime(timeLineModel.getStart_time(),
                            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                            DATETIME_FORMAT)));
        }
        else
            holder.tvBeginTime.setVisibility(View.GONE);

        //结束时间
        if(!TextUtils.isEmpty(timeLineModel.getEnd_time())) {
            holder.tvEndTime.setVisibility(View.VISIBLE);
            //hh:mm a, dd-MMM-yyyy
            holder.tvEndTime.setText(mContext.getResources().getString(R.string.begin_end_fmt,
                    DateTimeUtils.parseDateTime(timeLineModel.getEnd_time(),
                            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                            DATETIME_FORMAT)));
        }
        else
            holder.tvEndTime.setVisibility(View.GONE);

        //描述信息
        if (!TextUtils.isEmpty(timeLineModel.getDescription())) {
            holder.tvDesc.setText(timeLineModel.getDescription());
        } else {
//            holder.tvDesc.setText("--");
            holder.tvDesc.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

}

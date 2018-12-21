package com.cheersmind.cheersgenie.features.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.main.entity.ExamEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 历史测评recycler适配器
 */
public class HistoryExamRecyclerAdapter extends BaseQuickAdapter<ExamEntity, BaseViewHolder> {

    private SimpleDateFormat formatIso8601;
    private SimpleDateFormat formatNormal;

    private Context context;

    public HistoryExamRecyclerAdapter(Context context , int layoutResId, @Nullable List<ExamEntity> data) {
        super(layoutResId, data);
        this.context = context;
        formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        formatNormal = new SimpleDateFormat("yyyy年 MM月 dd日");
    }

    @Override
    protected void convert(BaseViewHolder helper, ExamEntity item) {

        //测评名称
        if (!TextUtils.isEmpty(item.getExamName())) {
            helper.setText(R.id.tv_exam_name, item.getExamName());
        } else {
            helper.setText(R.id.tv_exam_name, "");
        }

        //专题名称
        if (!TextUtils.isEmpty(item.getSeminarName())) {
            helper.setText(R.id.tv_seminar, item.getSeminarName());
        } else {
            helper.getView(R.id.tv_seminar).setVisibility(View.GONE);
        }

        //话题完成数量
        if (item.getTotalTopics() > 0) {
            helper.setText(R.id.tv_topic_count, context.getResources()
                    .getString(R.string.complete_topics_count_summary,
                            String.valueOf(item.getCompleteTopics()),
                            String.valueOf(item.getTotalTopics())));
        } else {
            helper.getView(R.id.tv_topic_count).setVisibility(View.GONE);
        }

        //孩子测评状态
        int status = item.getChildExamStatus();
        helper.getView(R.id.iv_status).setVisibility(View.VISIBLE);
        if(status == Dictionary.CHILD_EXAM_STATUS_COMPLETE) {
            helper.setImageResource(R.id.iv_status, R.drawable.exam_status_complete);

        } else {
            //测评状态
            int examStatus = item.getStatus();
            //非未开始的才显示未完成状态
            if (examStatus != Dictionary.EXAM_STATUS_INACTIVE) {
                helper.setImageResource(R.id.iv_status, R.drawable.exam_status_no_complete);
            } else {
                helper.getView(R.id.iv_status).setVisibility(View.GONE);
            }
        }

        //开始时间
        if(!TextUtils.isEmpty(item.getStartTime())) {
            String startTime = item.getStartTime();//ISO8601 时间字符串
            try {
                Date startDate = formatIso8601.parse(startTime);
                String startDateStr = formatNormal.format(startDate);
                helper.setText(R.id.tv_begin_time, startDateStr);

            } catch (ParseException e) {
                e.printStackTrace();
                helper.setText(R.id.tv_begin_time, "");
            }
        }
        else
            helper.setText(R.id.tv_begin_time, "");

        //结束时间
        if(!TextUtils.isEmpty(item.getEndTime())) {
            String endTime = item.getEndTime();//ISO8601 时间字符串
            try {
                Date endDate = formatIso8601.parse(endTime);
                String endDateStr = formatNormal.format(endDate);
                helper.setText(R.id.tv_end_time, endDateStr);

            } catch (ParseException e) {
                e.printStackTrace();
                helper.setText(R.id.tv_end_time, "");
            }
        }
        else
            helper.setText(R.id.tv_end_time, "");


    }
}

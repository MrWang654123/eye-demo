package com.cheersmind.cheersgenie.features.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.utils.DateTimeUtils;
import com.cheersmind.cheersgenie.main.entity.ExamEntity;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 历史测评recycler适配器
 */
public class HistoryExamRecyclerAdapter extends BaseQuickAdapter<ExamEntity, BaseViewHolder> {

    SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy-MM-dd");


    public HistoryExamRecyclerAdapter(Context context , int layoutResId, @Nullable List<ExamEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExamEntity item) {

        //状态
        int status = item.getStatus();
        if(status == Dictionary.TASK_STATUS_COMPLETE) {
            helper.setImageResource(R.id.iv_status, R.drawable.exam_status_complete);

        } else if(status == Dictionary.TASK_STATUS_DOING) {
            helper.setImageResource(R.id.iv_status, R.drawable.exam_status_doing);

        } else if(status == Dictionary.TASK_STATUS_INACTIVE) {
            helper.setImageResource(R.id.iv_status, R.drawable.exam_status_inactive);
        } else {
            helper.getView(R.id.tv_status).setVisibility(View.GONE);
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

        //描述信息
        if (!TextUtils.isEmpty(item.getExamName())) {
            helper.setText(R.id.tv_exam_name, item.getExamName());
        } else {
            helper.setText(R.id.tv_exam_name, "");
        }

    }
}

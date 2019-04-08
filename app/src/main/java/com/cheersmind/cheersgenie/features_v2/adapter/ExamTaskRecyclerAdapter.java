package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskChildEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 测评任务recycler适配器
 */
public class ExamTaskRecyclerAdapter extends BaseQuickAdapter<ExamTaskEntity, BaseViewHolder> {

    public ExamTaskRecyclerAdapter(Context context, int layoutResId, @Nullable List<ExamTaskEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExamTaskEntity item) {
        //标题
        helper.setText(R.id.tv_title, !TextUtils.isEmpty(item.getTask_name()) ? item.getTask_name().trim() : "");

        //简介
        helper.setText(R.id.tv_desc, item.getDescription());

        //主图
        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
        imageView.setImageURI(item.getTask_icon());

        //是否必修
        if (item.getRequired() == 0) {
            helper.getView(R.id.tv_required).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.tv_required).setVisibility(View.VISIBLE);
        }

        //状态
        ExamTaskChildEntity childTask = item.getChildTask();
        //进行中图标
        if (childTask != null) {
            if (childTask.getStatus() == Dictionary.TASK_STATUS_INCOMPLETE) {
                helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.tv_status).setVisibility(View.GONE);
            }

        } else {
            helper.getView(R.id.tv_status).setVisibility(View.GONE);
        }

        //共计任务项数量
        int taskItemCount = item.getExam_num() + item.getArticle_num() + item.getVideo_num() + item.getAudio_num();
        helper.setText(R.id.tv_task_item_count, "共计" + taskItemCount + "项任务");

        //参与人数
        helper.setText(R.id.tv_user_count, item.getUse_count() + "人参与");

        //内容示意小图标
        //文章
        if (item.getArticle_num() > 0) {
            helper.getView(R.id.iv_article).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_article).setVisibility(View.GONE);
        }
        //视频
        if (item.getVideo_num() > 0) {
            helper.getView(R.id.iv_video).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_video).setVisibility(View.GONE);
        }
        //音频
        if (item.getAudio_num() > 0) {
            helper.getView(R.id.iv_audio).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_audio).setVisibility(View.GONE);
        }
        //话题
        if (item.getExam_num() > 0) {
            helper.getView(R.id.iv_topic).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_topic).setVisibility(View.GONE);
        }
        //实践
        helper.getView(R.id.iv_practice).setVisibility(View.GONE);

        //是否被锁
        if (item.getIs_lock() == Dictionary.IS_LOCKED_YSE) {
            helper.getView(R.id.iv_lock).setVisibility(View.VISIBLE);

        } else {
            helper.getView(R.id.iv_lock).setVisibility(View.GONE);
        }

        //是否是校本课程
        if (item.getFrom_type() == Dictionary.TASK_FROM_TYPE_SCHOOL_COURSE) {
            helper.getView(R.id.iv_school_course).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_school_course).setVisibility(View.GONE);
        }

        //状态不同，颜色不同
        if (childTask != null) {
            //完成状态
            if (childTask.getStatus() == Dictionary.TASK_STATUS_COMPLETED) {
                ((TextView) helper.getView(R.id.tv_title)).setTextColor(0xff999999);
            } else {
                ((TextView) helper.getView(R.id.tv_title)).setTextColor(0xff444444);
            }
        }
    }

}

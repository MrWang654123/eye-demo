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

    private boolean addTask;

    public ExamTaskRecyclerAdapter(Context context, int layoutResId, @Nullable List<ExamTaskEntity> data) {
        super(layoutResId, data);
        this.addTask = false;
    }

    public ExamTaskRecyclerAdapter(Context context, int layoutResId, @Nullable List<ExamTaskEntity> data, boolean addTask) {
        super(layoutResId, data);
        this.addTask = addTask;
    }

    @Override
    protected void convert(BaseViewHolder helper, ExamTaskEntity item) {
        //标题
        helper.setText(R.id.tv_title, !TextUtils.isEmpty(item.getTask_name()) ? item.getTask_name().trim() : "");

        //简介
        helper.setText(R.id.tv_desc, !TextUtils.isEmpty(item.getDescription()) ? item.getDescription() : "暂无描述信息");

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
        if (childTask != null) {
            //进行中
            if (childTask.getStatus() == Dictionary.TASK_STATUS_INCOMPLETE) {
                helper.getView(R.id.tv_status_doing).setVisibility(View.VISIBLE);
                helper.getView(R.id.tv_status_complete).setVisibility(View.GONE);

            } else if (childTask.getStatus() == Dictionary.TASK_STATUS_COMPLETED) {//已完成
                helper.getView(R.id.tv_status_doing).setVisibility(View.GONE);
                helper.getView(R.id.tv_status_complete).setVisibility(View.VISIBLE);

            } else if (childTask.getStatus() == Dictionary.TASK_STATUS_OVER_INCOMPLETE) {//已结束未完成
                helper.getView(R.id.tv_status_doing).setVisibility(View.GONE);
                helper.getView(R.id.tv_status_complete).setVisibility(View.GONE);

            } else {
                helper.getView(R.id.tv_status_doing).setVisibility(View.GONE);
                helper.getView(R.id.tv_status_complete).setVisibility(View.GONE);
            }

        } else {
            helper.getView(R.id.tv_status_doing).setVisibility(View.GONE);
            helper.getView(R.id.tv_status_complete).setVisibility(View.GONE);
        }

        //共计任务项数量
        int taskItemCount = item.getExam_num() + item.getArticle_num() + item.getVideo_num() + item.getAudio_num();
        helper.setText(R.id.tv_task_item_count, "共" + taskItemCount + "个任务");

        //参与人数
        helper.setText(R.id.tv_user_count, item.getUse_count() + "人完成");

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

//        //状态不同，颜色不同
//        if (childTask != null) {
//            //完成状态
//            if (childTask.getStatus() == Dictionary.TASK_STATUS_COMPLETED) {
//                ((TextView) helper.getView(R.id.tv_title)).setTextColor(0xffaaaaaa);
//            } else {
//                ((TextView) helper.getView(R.id.tv_title)).setTextColor(0xff444444);
//            }
//        }

        if (addTask) {
            //选中图标
            SimpleDraweeView ivSelect = helper.getView(R.id.iv_select);
            ivSelect.setVisibility(View.VISIBLE);
            if (item.isSelected()) {
                ivSelect.setImageResource(R.drawable.check_box_outline);

            } else {
                ivSelect.setImageResource(R.drawable.check_box_outline_bl);
            }
        }

    }

}

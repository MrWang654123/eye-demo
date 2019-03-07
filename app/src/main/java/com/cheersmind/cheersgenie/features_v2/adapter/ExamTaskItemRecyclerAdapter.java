package com.cheersmind.cheersgenie.features_v2.adapter;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskItemEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 测评任务内容项recycler适配器
 */
public class ExamTaskItemRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    //任务状态
    protected int taskStatus;

    public ExamTaskItemRecyclerAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(ExamTaskItemEntity.TYPE_EXAM, R.layout.recycleritem_exam_task_item_exam);
        addItemType(ExamTaskItemEntity.TYPE_DIMENSION, R.layout.recycleritem_exam_task_item_dimension);
        addItemType(ExamTaskItemEntity.TYPE_ARTICLE, R.layout.recycleritem_exam_task_item_article);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            //测评
            case ExamTaskItemEntity.TYPE_EXAM: {
                ExamTaskItemEntity taskItem = (ExamTaskItemEntity) item;
                //标题
                helper.setText(R.id.tv_title, taskItem.getItem_name());
                //提示图标
                SimpleDraweeView imageView = helper.getView(R.id.iv_tip);
                imageView.setActualImageResource(R.drawable.ic_arrow_drop_down);
                //使用人数
                helper.setText(R.id.tv_count, getRecyclerView().getContext().getString(R.string.do_count, String.valueOf(taskItem.getUse_count())));
                //类型
                helper.setText(R.id.tv_type, "测评");
                break;
            }
            //量表
            case ExamTaskItemEntity.TYPE_DIMENSION: {
                DimensionInfoEntity dimensionInfo = (DimensionInfoEntity) item;

                //最后一个量表
                if (dimensionInfo.isLastInTopic()) {
                    helper.getView(R.id.cl_content).setBackgroundResource(R.drawable.exam_item_footer);
                    helper.getView(R.id.tv_footer_padding).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.cl_content).setBackgroundResource(R.drawable.exam_item_body);
                    helper.getView(R.id.tv_footer_padding).setVisibility(View.GONE);
                }

                //标题
                helper.setText(R.id.tv_title2, dimensionInfo.getDimensionName());

                //使用人数（0隐藏）
                if (dimensionInfo.getUseCount() > 0) {
                    String useCount = "• " + getRecyclerView().getResources().getString(R.string.exam_dimension_use_count, dimensionInfo.getUseCount() + "");
                    helper.getView(R.id.tv_used_count).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_used_count, useCount);
                } else {
                    helper.getView(R.id.tv_used_count).setVisibility(View.GONE);
                }

                //描述
                if (!TextUtils.isEmpty(dimensionInfo.getDescription())) {
                    helper.getView(R.id.tv_desc).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_desc, dimensionInfo.getDescription());
                } else {
                    //被锁
                    if (dimensionInfo.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
                        helper.getView(R.id.tv_desc).setVisibility(View.INVISIBLE);
                    } else {
                        helper.getView(R.id.tv_desc).setVisibility(View.GONE);
                    }
                }

                //状态
                DimensionInfoChildEntity childDimension = dimensionInfo.getChildDimension();
                if (childDimension == null) {
                    //未开始
                    helper.getView(R.id.tv_status).setVisibility(View.GONE);
                    ((TextView)helper.getView(R.id.tv_status)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    helper.setText(R.id.tv_status, "未开始");

                } else if (childDimension.getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                    //任务已结束则不显示进行中的状态
                    if (taskStatus == Dictionary.TASK_STATUS_OVER_INCOMPLETE) {
                        helper.getView(R.id.tv_status).setVisibility(View.GONE);
                    } else {
                        //进行中
                        helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
                        ((TextView) helper.getView(R.id.tv_status)).setCompoundDrawablesWithIntrinsicBounds(
                                ContextCompat.getDrawable(getRecyclerView().getContext(), R.drawable.doing), null, null, null);
                        helper.setText(R.id.tv_status, "进行中");
                    }

                } else if (childDimension.getStatus() == Dictionary.DIMENSION_STATUS_COMPLETE) {
                    //已完成
                    helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
                    ((TextView)helper.getView(R.id.tv_status)).setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(getRecyclerView().getContext(), R.drawable.complete), null, null, null);
                    helper.setText(R.id.tv_status, "已完成");
                }

                //加载图片
                SimpleDraweeView imageView = helper.getView(R.id.iv_icon);
                imageView.setImageURI(dimensionInfo.getIcon());

                //是否被锁，显隐锁图标
                if (dimensionInfo.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
                    (helper.getView(R.id.iv_lock)).setVisibility(View.VISIBLE);
                } else {
                    (helper.getView(R.id.iv_lock)).setVisibility(View.GONE);
                }

                //把量表对象设置到子项视图的tag中
                helper.itemView.setTag(dimensionInfo);

                break;
            }
            //文章
            case ExamTaskItemEntity.TYPE_ARTICLE: {
                ExamTaskItemEntity taskItem = (ExamTaskItemEntity) item;
                //标题
                helper.setText(R.id.tv_title, taskItem.getItem_name());
                //提示图标
                SimpleDraweeView imageView = helper.getView(R.id.iv_tip);
                imageView.setActualImageResource(R.drawable.jz_play_normal);
                //使用人数
                helper.setText(R.id.tv_count, getRecyclerView().getContext().getString(R.string.do_count, String.valueOf(taskItem.getUse_count())));
                //类型
                switch (taskItem.getItem_type()) {
                    case 3: {
                        helper.setText(R.id.tv_type, "文章");
                        helper.getView(R.id.tv_duration).setVisibility(View.GONE);
                        break;
                    }
                    case 4: {
                        helper.setText(R.id.tv_type, "视频");
                        //预计时间
                        int minute = taskItem.getTime_long() / 60;
                        int second = taskItem.getTime_long() % 60;
                        String durationStr = minute + "分" + second + "秒";
                        helper.getView(R.id.tv_duration).setVisibility(View.VISIBLE);
                        helper.setText(R.id.tv_duration, durationStr);
                        break;
                    }
                    case 5: {
                        helper.setText(R.id.tv_type, "音频");
                        //预计时间
                        int minute = taskItem.getTime_long() / 60;
                        int second = taskItem.getTime_long() % 60;
                        String durationStr = minute + "分" + second + "秒";
                        helper.getView(R.id.tv_duration).setVisibility(View.VISIBLE);
                        helper.setText(R.id.tv_duration, durationStr);
                        break;
                    }
                    default: {
                        helper.setText(R.id.tv_type, "其他");
                    }
                }

                break;
            }
        }

    }

    /**
     * 任务状态
     * @param taskStatus 状态
     */
    public ExamTaskItemRecyclerAdapter setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
        return this;
    }
}

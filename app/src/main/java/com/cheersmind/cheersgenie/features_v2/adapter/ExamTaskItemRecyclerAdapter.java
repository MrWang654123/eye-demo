package com.cheersmind.cheersgenie.features_v2.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskItemEntity;
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
        addItemType(Dictionary.TASK_ITEM_TYPE_TOPIC_COMMON, R.layout.recycleritem_exam_task_item_topic);
        addItemType(Dictionary.TASK_ITEM_TYPE_DIMENSION, R.layout.recycleritem_exam_task_item_dimension);
        addItemType(Dictionary.TASK_ITEM_TYPE_ARTICLE, R.layout.recycleritem_exam_task_item_article);
        addItemType(Dictionary.TASK_ITEM_TYPE_VIDEO, R.layout.recycleritem_exam_task_item_article);
        addItemType(Dictionary.TASK_ITEM_TYPE_AUDIO, R.layout.recycleritem_exam_task_item_article);
        addItemType(Dictionary.TASK_ITEM_TYPE_PRACTICE, R.layout.recycleritem_exam_task_item_article);
        addItemType(Dictionary.TASK_ITEM_TYPE_CHOOSE_COURSE, R.layout.recycleritem_exam_task_item_article);
        addItemType(Dictionary.TASK_ITEM_TYPE_TOPIC_363, R.layout.recycleritem_exam_task_item_topic);
        addItemType(Dictionary.TASK_ITEM_TYPE_TOPIC_373, R.layout.recycleritem_exam_task_item_topic);
        addItemType(Dictionary.TASK_ITEM_TYPE_TOPIC_321_42, R.layout.recycleritem_exam_task_item_topic);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            //话题
            case Dictionary.TASK_ITEM_TYPE_TOPIC_COMMON:
            case Dictionary.TASK_ITEM_TYPE_TOPIC_363:
            case Dictionary.TASK_ITEM_TYPE_TOPIC_373:
            case Dictionary.TASK_ITEM_TYPE_TOPIC_321_42: {
                ExamTaskItemEntity taskItem = (ExamTaskItemEntity) item;
                //标题
                helper.setText(R.id.tv_title, taskItem.getItem_name());
                //提示图标
                SimpleDraweeView imageView = helper.getView(R.id.iv_tip);
                imageView.setActualImageResource(R.drawable.mine_exam);
                //量表数量（目前无该数据）
                helper.getView(R.id.tv_dimension_count).setVisibility(View.GONE);
                //使用人数
                helper.setText(R.id.tv_count, getRecyclerView().getContext().getString(R.string.do_count, String.valueOf(taskItem.getUse_count())));
                //类型
                helper.setText(R.id.tv_type, "测评");
                //是否完成
//                if (taskItem.getChildItem() != null && taskItem.getChildItem().getStatus() == Dictionary.TASK_STATUS_COMPLETED) {
//                    helper.getView(R.id.btn_report).setVisibility(View.VISIBLE);
//                } else {
//                    helper.getView(R.id.btn_report).setVisibility(View.GONE);
//                }
                //是否被锁
                if (taskItem.getIs_lock() == Dictionary.IS_LOCKED_NO) {
                    helper.getView(R.id.iv_lock).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.iv_lock).setVisibility(View.VISIBLE);
                }

                break;
            }
            //量表
            case Dictionary.TASK_ITEM_TYPE_DIMENSION: {
                DimensionInfoEntity dimensionInfo = (DimensionInfoEntity) item;

                //第一个量表
                if (dimensionInfo.isFirstInTopic()) {
                    helper.getView(R.id.simulate_padding_top).setVisibility(View.VISIBLE);

                } else {
                    helper.getView(R.id.simulate_padding_top).setVisibility(View.GONE);
                }

                //最后一个量表
                if (dimensionInfo.isLastInTopic()) {
                    helper.getView(R.id.simulate_padding_bottom).setVisibility(View.VISIBLE);
                    helper.getView(R.id.divider).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tv_timeline_down).setVisibility(View.GONE);

                } else {
                    helper.getView(R.id.simulate_padding_bottom).setVisibility(View.GONE);
                    helper.getView(R.id.divider).setVisibility(View.GONE);
                    helper.getView(R.id.tv_timeline_down).setVisibility(View.VISIBLE);
                }

                //标题
                helper.setText(R.id.tv_title, dimensionInfo.getDimensionName());

                //是否被锁，显隐锁图标
                if (dimensionInfo.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
                    (helper.getView(R.id.iv_lock)).setVisibility(View.VISIBLE);
                } else {
                    (helper.getView(R.id.iv_lock)).setVisibility(View.GONE);
                }

                break;
            }
            //文章
            case Dictionary.TASK_ITEM_TYPE_ARTICLE:
            case Dictionary.TASK_ITEM_TYPE_VIDEO:
            case Dictionary.TASK_ITEM_TYPE_AUDIO:
            case Dictionary.TASK_ITEM_TYPE_PRACTICE:
            case Dictionary.TASK_ITEM_TYPE_CHOOSE_COURSE: {
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

                //是否被锁
                if (taskItem.getIs_lock() == Dictionary.IS_LOCKED_NO) {
                    helper.getView(R.id.iv_lock).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.iv_lock).setVisibility(View.VISIBLE);
                }

                break;
            }
        }

        //任务项
        if (item instanceof ExamTaskItemEntity) {
            ExamTaskItemEntity taskItem = (ExamTaskItemEntity) item;
            //状态不同，颜色不同：完成状态
            if (taskItem.getChildItem() != null && taskItem.getChildItem().getStatus() == Dictionary.TASK_STATUS_COMPLETED) {
                ((TextView) helper.getView(R.id.tv_title)).setTextColor(0xff999999);
            } else {
                ((TextView) helper.getView(R.id.tv_title)).setTextColor(0xff444444);
            }

        } else if (item instanceof DimensionInfoEntity) {//量表
            DimensionInfoEntity dimensionInfo = (DimensionInfoEntity) item;
            //状态不同，颜色不同：完成状态
            if (dimensionInfo.getChildDimension() != null
                    && dimensionInfo.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_COMPLETE) {
                ((TextView) helper.getView(R.id.tv_title)).setTextColor(0xff999999);
            } else {
                ((TextView) helper.getView(R.id.tv_title)).setTextColor(0xff444444);
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

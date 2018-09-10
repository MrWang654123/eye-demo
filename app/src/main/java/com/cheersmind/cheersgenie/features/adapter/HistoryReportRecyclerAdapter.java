package com.cheersmind.cheersgenie.features.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.entity.HistoryReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.IntegralEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 历史报告记录recycler适配器
 */
public class HistoryReportRecyclerAdapter extends BaseMultiItemQuickAdapter<HistoryReportItemEntity, BaseViewHolder> {

    public HistoryReportRecyclerAdapter(List<HistoryReportItemEntity> data) {
        super(data);
        addItemType(HistoryReportItemEntity.HEAD, R.layout.recycleritem_history_report_head);
        addItemType(HistoryReportItemEntity.ITEM, R.layout.recycleritem_history_report);
    }

    @Override
    protected void convert(BaseViewHolder helper, HistoryReportItemEntity item) {
        switch (helper.getItemViewType()) {
            //表格head
            case HistoryReportItemEntity.HEAD: {
                helper.setText(R.id.tv_patch, "测评批次");
                helper.setText(R.id.tv_result_simple, "推荐课程");
                helper.setText(R.id.tv_goto_detail, "明细");
                break;
            }
            //表格body
            case HistoryReportItemEntity.ITEM: {
                helper.setText(R.id.tv_patch, item.getBatch());
                helper.setText(R.id.tv_result_simple, "#物理、地理、生物");
                helper.setText(R.id.tv_goto_detail, "查看");
                //查看点击监听
                helper.addOnClickListener(R.id.tv_goto_detail);
                break;
            }
        }

    }

}

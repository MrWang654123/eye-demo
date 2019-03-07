package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.ExamModuleEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 功能：测评模块recycler适配器
 * 日期：2019年2月13日 上午9:40:10
 * 作者：奇思科技-Shiro
 */
public class ExamModuleRecyclerAdapter extends BaseQuickAdapter<ExamModuleEntity, BaseViewHolder> {

    SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy/MM/dd");

    public ExamModuleRecyclerAdapter(Context context, int layoutResId, @Nullable List<ExamModuleEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExamModuleEntity item) {
        //标题
        helper.setText(R.id.tv_title, item.getModule_name());

        //主图
        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
        imageView.setImageURI(item.getModule_icon());

        try {
            Date startDate = formatIso8601.parse(item.getStart_time());
            Date endDate = formatIso8601.parse(item.getEnd_time());
            String startDateStr = formatNormal.format(startDate);
            String endDateStr = formatNormal.format(endDate);

            helper.setText(R.id.tv_begin_time, startDateStr);
            helper.setText(R.id.tv_end_time, endDateStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}

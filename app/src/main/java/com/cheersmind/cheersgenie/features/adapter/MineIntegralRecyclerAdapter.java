package com.cheersmind.cheersgenie.features.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.entity.IntegralEntity;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * 积分recycler适配器
 */
public class MineIntegralRecyclerAdapter extends BaseQuickAdapter<IntegralEntity, BaseViewHolder> {

    public MineIntegralRecyclerAdapter(int layoutResId, @Nullable List<IntegralEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, IntegralEntity item) {
        //积分名称
        helper.setText(R.id.tv_title, item.getIntegralName());

        //积分变动分数
        int scoreVal = item.getIntegralScore();
        String scoreStr = "";
        if (scoreVal == 0) {
            scoreStr = "0";
        } else if (scoreVal > 0) {
            scoreStr = "+" + scoreVal;
        } else {
            scoreStr = "" + scoreVal;
        }
        helper.setText(R.id.tv_integral_change, scoreStr);

        //时间
        String dateStr = item.getCreateTime();//ISO8601 时间字符串
        SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date date = formatIso8601.parse(dateStr);
            SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String normalDateStr = formatNormal.format(date);
            helper.setText(R.id.tv_datetime, normalDateStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}

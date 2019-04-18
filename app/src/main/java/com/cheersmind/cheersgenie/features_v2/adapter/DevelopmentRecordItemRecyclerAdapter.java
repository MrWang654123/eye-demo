package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.DevelopmentRecordItem;
import com.cheersmind.cheersgenie.features_v2.view.CircleScaleView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * 发展档案子项recycler适配器
 */
public class DevelopmentRecordItemRecyclerAdapter extends BaseQuickAdapter<DevelopmentRecordItem, BaseViewHolder> {

    private int[] colorList = {0xff6d9bf1, 0xfffe8c0b, 0xff35eb93, 0xffc35be9, 0xfff16d9f};

    //默认强调色
    private int accentColor;
    //可控制是否四舍五入
    private NumberFormat nf;

    DevelopmentRecordItemRecyclerAdapter(Context context, int layoutResId, @Nullable List<DevelopmentRecordItem> data) {
        super(layoutResId, data);

        accentColor = context.getResources().getColor(R.color.colorAccent);

        nf = NumberFormat.getNumberInstance();
        // 保留一位小数
        nf.setMaximumFractionDigits(1);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.DOWN);
    }

    @Override
    protected void convert(BaseViewHolder helper, DevelopmentRecordItem item) {

        helper.getView(R.id.ll_score).setVisibility(View.GONE);
        helper.getView(R.id.tv_score_desc).setVisibility(View.GONE);
        helper.getView(R.id.tv_dimension_original_score).setVisibility(View.GONE);
        helper.getView(R.id.tv_dimension_no_score_result).setVisibility(View.GONE);
        helper.getView(R.id.ll_no_data).setVisibility(View.GONE);

        //前缀视图颜色
        int colorIndex = item.getIndex() % colorList.length;
        helper.getView(R.id.view_pre).setBackgroundColor(colorList[colorIndex]);
        //标题
        helper.setText(R.id.tv_title, item.getDimension_name());

        //量表：如果没有T分数和结论，则显示原始分；学业兴趣和职业兴趣没有分数和排名，则显示result
        //有排名（T分数才有排名）
        if (item.getRank() != null) {
            helper.getView(R.id.ll_score).setVisibility(View.VISIBLE);
            helper.getView(R.id.tv_score_desc).setVisibility(View.VISIBLE);
            double score = item.getScore() != null ? new BigDecimal(item.getScore()).setScale(1, BigDecimal.ROUND_DOWN).doubleValue() : 0;
            //分数
            String scoreStr = String.valueOf(score);
            helper.setText(R.id.tv_score, scoreStr);
            //分数圆环
            CircleScaleView scaleView = helper.getView(R.id.csv_score);
            scaleView.setSecondColor(colorList[colorIndex]);
            scaleView.setCirclePercent((float)(100 - score), (float) score);
            //结果
            helper.setText(R.id.tv_result, item.getResult());
            //分数描述
            String scoreDesc = "本测评中你的得分<b><font color='" + accentColor + "'>" + scoreStr +
                    "分</font></b>,高于<b><font color='" + accentColor + "'>" +
                    String.format(Locale.CHINA,"%.1f", item.getRank() * 100) + "%</font></b>的用户";
            helper.setText(R.id.tv_score_desc, Html.fromHtml(scoreDesc));

        } else {
            //有分数
            if (item.getScore() != null) {
                //原始分
                helper.getView(R.id.tv_dimension_original_score).setVisibility(View.VISIBLE);
                String scoreStr = "本测评中你的得分为<b><font color='" + accentColor + "'>" +
                        nf.format(item.getScore()) + "分</font></b>";
                helper.setText(R.id.tv_dimension_original_score, Html.fromHtml(scoreStr));

            } else {//无分数
                //有结果
                if (!TextUtils.isEmpty(item.getResult())) {
                    helper.getView(R.id.tv_dimension_no_score_result).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_dimension_no_score_result, item.getResult());

                } else {//无结果
                    helper.getView(R.id.ll_no_data).setVisibility(View.VISIBLE);
                }
            }
        }
    }

}

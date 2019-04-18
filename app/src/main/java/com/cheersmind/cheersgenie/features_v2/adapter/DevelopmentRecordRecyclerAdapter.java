package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.entity.DevelopmentRecord;
import com.cheersmind.cheersgenie.features_v2.entity.DevelopmentRecordItem;
import com.cheersmind.cheersgenie.features_v2.entity.DevelopmentRecordRecycler;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamReportActivity;
import com.cheersmind.cheersgenie.features_v2.view.CircleScaleView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * 发展档案recycler适配器
 */
public class DevelopmentRecordRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private Context context;

    //概要
    public static final int LAYOUT_TYPE_SUMMARY = 1;
    //普通项
    public static final int LAYOUT_TYPE_COMMON_ITEM = 2;
    //横向recyclerView
    public static final int LAYOUT_TYPE_H_RECYCLER = 3;

    //默认强调色
    private int accentColor;
    //可控制是否四舍五入
    private NumberFormat nf;

    //recycler item点击监听
    private BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            DevelopmentRecordItem entity = ((DevelopmentRecordItemRecyclerAdapter)adapter).getData().get(position);
            //孩子测评ID、话题量表ID不为空
            if (entity.isFinish()
                    && !TextUtils.isEmpty(entity.getChild_exam_id())
                    && !TextUtils.isEmpty(entity.getTopic_dimension_id())) {
                ExamReportDto dto = new ExamReportDto();
                dto.setChildExamId(entity.getChild_exam_id());//孩子测评ID
                dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国
                dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);//量表报告类型
                dto.setRelationId(entity.getTopic_dimension_id());//话题量表ID
                //查看报告
                ExamReportActivity.startExamReportActivity(context, dto);

            } else {
                Toast.makeText(context,"继续完成", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public DevelopmentRecordRecyclerAdapter(Context context, List<MultiItemEntity> data) {
        super(data);
        addItemType(LAYOUT_TYPE_SUMMARY, R.layout.recycleritem_development_record_header);
        addItemType(LAYOUT_TYPE_COMMON_ITEM, R.layout.recycleritem_development_record_item);
        addItemType(LAYOUT_TYPE_H_RECYCLER, R.layout.recycleritem_development_record_h_recycler);

        accentColor = context.getResources().getColor(R.color.colorAccent);

        nf = NumberFormat.getNumberInstance();
        // 保留一位小数
        nf.setMaximumFractionDigits(1);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.DOWN);

        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            //概要
            case LAYOUT_TYPE_SUMMARY: {
                DevelopmentRecord entity = (DevelopmentRecord) item;
                //标题
                helper.setText(R.id.tv_title, entity.getCapability_name());
                //完成度
//                helper.setText(R.id.tv_ratio, "已完成" + nf.format(entity.getFinish_radio() * 100) + "%");
                //伸缩图标
//                helper.setImageResource(R.id.iv_expand, entity.isExpanded() ? R.drawable.ic_arrow_drop_up_black_24dp : R.drawable.ic_arrow_drop_down_black_24dp);

                //背景图
//                SimpleDraweeView imageView = helper.getView(R.id.iv_main);
////                imageView.setImageURI(item.getArticleImg());
//                imageView.setImageResource(R.drawable.default_image_round);
                break;
            }
            //横向Recycler
            case LAYOUT_TYPE_H_RECYCLER: {
                DevelopmentRecordRecycler entity = (DevelopmentRecordRecycler) item;
                RecyclerView recyclerView = helper.getView(R.id.recyclerView);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                }
                BaseQuickAdapter adapter = (BaseQuickAdapter) recyclerView.getAdapter();
                if (adapter == null) {
                    adapter = new DevelopmentRecordItemRecyclerAdapter(context, R.layout.recycleritem_development_record_item_h_recycler, entity.getItems());
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.setNewData(entity.getItems());
                }

                //点击监听
                OnItemClickListener onItemClickListener = adapter.getOnItemClickListener();
                if (onItemClickListener == null) {
                    adapter.setOnItemClickListener(recyclerItemClickListener);
                }

//                DevelopmentRecordRecycler entity = (DevelopmentRecordRecycler) item;
//                RecyclerView recyclerView = helper.getView(R.id.recyclerView);
//                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                BaseQuickAdapter adapter = new DevelopmentRecordItemRecyclerAdapter(context, R.layout.recycleritem_development_record_item_h_recycler, entity.getItems());
//                recyclerView.setAdapter(adapter);

                break;
            }
            //普通项
            case LAYOUT_TYPE_COMMON_ITEM: {
                DevelopmentRecordItem entity = (DevelopmentRecordItem) item;

                helper.getView(R.id.ll_score).setVisibility(View.GONE);
                helper.getView(R.id.tv_score_desc).setVisibility(View.GONE);
                helper.getView(R.id.tv_dimension_original_score).setVisibility(View.GONE);
                helper.getView(R.id.tv_dimension_no_score_result).setVisibility(View.GONE);
                helper.getView(R.id.tv_dimension_no_result_tip).setVisibility(View.GONE);

                //标题
                helper.setText(R.id.tv_title, (entity.getIndex() + 1) + "、" + entity.getDimension_name());

                //量表：如果没有T分数和结论，则显示原始分；学业兴趣和职业兴趣没有分数和排名，则显示result
                //有排名（T分数才有排名）
                if (entity.getRank() != null) {
                    helper.getView(R.id.ll_score).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tv_score_desc).setVisibility(View.VISIBLE);
                    double score = entity.getScore() != null ? new BigDecimal(entity.getScore()).setScale(1, BigDecimal.ROUND_DOWN).doubleValue() : 0;
                    //分数
                    String scoreStr = String.valueOf(score);
                    helper.setText(R.id.tv_score, scoreStr);
                    //分数圆环
                    ((CircleScaleView)helper.getView(R.id.csv_score)).setCirclePercent((float)(100 - score), (float) score);
                    //结果
                    helper.setText(R.id.tv_result, entity.getResult());
                    //分数描述
                    String scoreDesc = "本测评中你的得分<b><font color='" + accentColor + "'>" + scoreStr +
                            "分</font></b>,高于<b><font color='" + accentColor + "'>" +
                            String.format(Locale.CHINA,"%.1f", entity.getRank() * 100) + "%</font></b>的用户";
                    helper.setText(R.id.tv_score_desc, Html.fromHtml(scoreDesc));

                } else {
                    //有分数
                    if (entity.getScore() != null) {
                        //原始分
                        helper.getView(R.id.tv_dimension_original_score).setVisibility(View.VISIBLE);
                        String scoreStr = "本测评中你的得分为<b><font color='" + accentColor + "'>" +
                                nf.format(entity.getScore()) + "分</font></b>";
                        helper.setText(R.id.tv_dimension_original_score, Html.fromHtml(scoreStr));

                    } else {//无分数
                        //有结果
                        if (!TextUtils.isEmpty(entity.getResult())) {
                            helper.getView(R.id.tv_dimension_no_score_result).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tv_dimension_no_score_result, entity.getResult());

                        } else {//无结果
                            helper.getView(R.id.tv_dimension_no_result_tip).setVisibility(View.VISIBLE);
                        }
                    }
                }

//                //量表：没有T分数和结论，则显示原始分；学业兴趣和职业兴趣没有分数和排名，则显示result
//                //没有排名
//                if (entity.getRank() == null || entity.getRank() < 0.000001) {
//                    //有分数
//                    if (entity.getScore() != null && entity.getScore() > 0.000001) {
//                        //原始分
//                        helper.getView(R.id.tv_dimension_original_score).setVisibility(View.VISIBLE);
//                        String scoreStr = "本测评中你的个人原始得分为" + entity.getScore() + "分";
//                        helper.setText(R.id.tv_dimension_original_score, scoreStr);
//
//                    } else {//无分数
//                        //有结果
//                        if (!TextUtils.isEmpty(entity.getResult())) {
//                            helper.getView(R.id.tv_dimension_no_score_result).setVisibility(View.VISIBLE);
//                            helper.setText(R.id.tv_dimension_no_score_result, entity.getResult());
//
//                        } else {//无结果
//                            helper.getView(R.id.tv_dimension_no_result_tip).setVisibility(View.VISIBLE);
//                        }
//                    }
//
//                } else {//有排名则视为有结论
//                    helper.getView(R.id.ll_score).setVisibility(View.VISIBLE);
//                    helper.getView(R.id.tv_score_desc).setVisibility(View.VISIBLE);
//                    //分数
//                    String scoreStr = String.valueOf(Math.round(entity.getScore()));
//                    helper.setText(R.id.tv_score, scoreStr);
//                    //分数圆环
//                    ((CircleScaleView)helper.getView(R.id.csv_score)).setCirclePercent((float)(100 - entity.getScore()), entity.getScore().floatValue());
//                    //结果
//                    helper.setText(R.id.tv_result, entity.getResult());
//                    //分数描述
//                    String scoreDesc = "本测评中你的个人得分" + scoreStr + "分,高于" + String.format(Locale.CHINA,"%.1f", entity.getRank() * 100) + "%的用户";
//                    helper.setText(R.id.tv_score_desc, scoreDesc);
//                }
                break;
            }
        }

    }

}

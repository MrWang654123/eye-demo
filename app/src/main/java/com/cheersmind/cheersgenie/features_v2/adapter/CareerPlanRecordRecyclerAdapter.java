package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.WarpLinearLayout;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationRecord;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationRecordItem;
import com.cheersmind.cheersgenie.features_v2.entity.RecordItemHeader;
import com.cheersmind.cheersgenie.features_v2.entity.SelectCourseRecord;
import com.cheersmind.cheersgenie.features_v2.entity.SelectCourseRecordItem;
import com.cheersmind.cheersgenie.features_v2.entity.SimpleDimensionResult;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import java.util.List;

/**
 * 生涯规划档案recyclerView适配器
 */
public class CareerPlanRecordRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    //高考选科的header
    public static final int LAYOUT_SELECT_COURSE_HEADER = 1;
    //高考选科的item
    public static final int LAYOUT_SELECT_COURSE_ITEM = 2;
    //系统推荐选科的header
    public static final int LAYOUT_SYS_RECOMMEND_HEADER = 3;
    //系统推荐选科的item
    public static final int LAYOUT_SYS_RECOMMEND_ITEM = 4;
    //测评消息说明header
    public static final int LAYOUT_ITEM_HEADER = 5;
    //职业探索的header
    public static final int LAYOUT_OCCUPATION_EXPLORE_HEADER = 6;
    //职业探索的item
    public static final int LAYOUT_OCCUPATION_EXPLORE_ITEM = 7;
    //简单量表
    public static final int LAYOUT_SIMPLE_DIMENSION = 8;

    private Context context;

    public CareerPlanRecordRecyclerAdapter(Context context, List<MultiItemEntity> data) {
        super(data);
        this.context = context;

        //高考选科header
        addItemType(LAYOUT_SELECT_COURSE_HEADER, R.layout.recycleritem_career_record_select_course_header);
        //高考选科item
        addItemType(LAYOUT_SELECT_COURSE_ITEM, R.layout.recycleritem_career_record_select_course_item);
        //系统推荐选科header
        addItemType(LAYOUT_SYS_RECOMMEND_HEADER, R.layout.recycleritem_career_record_sys_recommed_header);
        //系统推荐选科item
        addItemType(LAYOUT_SYS_RECOMMEND_ITEM, R.layout.recycleritem_career_record_sys_recommed_item);
        //子项header
        addItemType(LAYOUT_ITEM_HEADER, R.layout.recycleritem_career_record_item_header);
        //职业探索header
        addItemType(LAYOUT_OCCUPATION_EXPLORE_HEADER, R.layout.recycleritem_career_record_occupation_explore_header);
        //职业探索item
        addItemType(LAYOUT_OCCUPATION_EXPLORE_ITEM, R.layout.recycleritem_career_record_occupation_explore_item);
        //简单量表
        addItemType(LAYOUT_SIMPLE_DIMENSION, R.layout.recycleritem_career_record_simple_dimension);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            //高考选科header
            case LAYOUT_SELECT_COURSE_HEADER: {
                SelectCourseRecord entity = (SelectCourseRecord) item;
                helper.getView(R.id.ll_has_result).setVisibility(View.GONE);
                helper.getView(R.id.ll_no_result).setVisibility(View.GONE);

                if (entity.isFinish()) {
                    helper.getView(R.id.ll_has_result).setVisibility(View.VISIBLE);
                    //推荐的科目
                    WarpLinearLayout layout = helper.getView(R.id.warpLinearLayout);
                    if (layout.getChildCount() == 0) {
                        for (String str : entity.getCustom_subjects()) {
                            TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.record_result_item, null);
                            tv.setText(str);
                            layout.addView(tv);
                        }
                    }

                    //可报考专业百分比
                    helper.setText(R.id.tv_can_select_major_ratio,
                            "-基于所选学科您可报考的专业占所有大学专业的" + entity.getCustom_major_per() * 100 + "%");
                    //要求较高专业百分比
                    helper.setText(R.id.tv_high_require_major_ratio,
                            "-对于所选学科要求较高的专业占所有大学专业的" + entity.getCustom_high_major_per() * 100 + "%");

                } else {
                    helper.getView(R.id.ll_no_result).setVisibility(View.VISIBLE);
                }
                break;
            }
            //高考选科item
            case LAYOUT_SELECT_COURSE_ITEM: {
                SelectCourseRecordItem entity = (SelectCourseRecordItem) item;
                helper.getView(R.id.ll_result_common).setVisibility(View.GONE);
                helper.getView(R.id.ll_no_complete).setVisibility(View.GONE);
                //标题
                helper.setText(R.id.tv_title, entity.getDimension_name());
                //通用结果
                if (ArrayListUtil.isEmpty(entity.getDimensions())) {
                    if (entity.isFinish()) {
                        helper.getView(R.id.btn_report).setVisibility(View.VISIBLE);
                        helper.getView(R.id.ll_result_common).setVisibility(View.VISIBLE);
                        //推荐
                        List<String> result = entity.getResult();
                        if (ArrayListUtil.isNotEmpty(result)) {
                            helper.getView(R.id.warpLinearLayout).setVisibility(View.VISIBLE);
                            WarpLinearLayout layout = helper.getView(R.id.warpLinearLayout);
                            //结果数量小于等于视图数量
                            if (result.size() <= layout.getChildCount()) {
                                for (int i=0; i<layout.getChildCount(); i++) {
                                    TextView textView = (TextView) layout.getChildAt(i);
                                    if (i < result.size()) {
                                        textView.setVisibility(View.VISIBLE);
                                        textView.setText(result.get(i));
                                    } else {
                                        textView.setVisibility(View.GONE);
                                    }
                                }
                            } else {//结果数量大于视图数量
                                int childCount = layout.getChildCount();
                                for (int i=0; i<childCount; i++) {
                                    TextView textView = (TextView) layout.getChildAt(i);
                                    textView.setVisibility(View.VISIBLE);
                                    textView.setText(result.get(i));
                                }

                                int addCount = result.size() - childCount;
                                for (int i=0; i<addCount; i++) {
                                    TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.record_result_item, null);
                                    tv.setText(result.get(childCount + i));
                                    layout.addView(tv);
                                }
                            }

                        } else {
                            helper.getView(R.id.warpLinearLayout).setVisibility(View.GONE);
                        }

                        //评价
                        if (!TextUtils.isEmpty(entity.getAppraisal())) {
                            helper.setText(R.id.tv_appraise, Html.fromHtml(entity.getAppraisal()));
                        } else {
                            helper.setText(R.id.tv_appraise, "暂无评价");
                        }

                    } else {
                        helper.getView(R.id.btn_report).setVisibility(View.GONE);
                        helper.getView(R.id.ll_no_complete).setVisibility(View.VISIBLE);
                    }
                }
                break;
            }
            //系统推荐选科header
            case LAYOUT_SYS_RECOMMEND_HEADER: {
                SelectCourseRecord entity = (SelectCourseRecord) item;
                if (entity.isFinish()) {
                    helper.getView(R.id.ll_result).setVisibility(View.VISIBLE);
                    WarpLinearLayout layout = helper.getView(R.id.warpLinearLayout);
                    if (layout.getChildCount() == 0) {
                        for (String str : entity.getRecommend_subjects()) {
                            TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.record_result_item, null);
                            tv.setText(str);
                            layout.addView(tv);
                        }

                    }
                } else {
                    helper.getView(R.id.ll_result).setVisibility(View.GONE);
                }
                break;
            }
            //系统推荐选科item
            case LAYOUT_SYS_RECOMMEND_ITEM: {
                SelectCourseRecord entity = (SelectCourseRecord) item;
                //可报考专业百分比
                helper.setText(R.id.tv_can_select_major_ratio,
                        "-基于所选学科您可报考的专业占所有大学专业的" + entity.getCustom_major_per() * 100 + "%");
                //要求较高专业百分比
                helper.setText(R.id.tv_high_require_major_ratio,
                        "-对于所选学科要求较高的专业占所有大学专业的" + entity.getCustom_high_major_per() * 100 + "%");
                break;
            }
            //子项header
            case LAYOUT_ITEM_HEADER: {
                RecordItemHeader entity = (RecordItemHeader) item;
                helper.setText(R.id.tv_title, entity.getTitle());
                //是否能够伸缩
                if (entity.isCanExpand()) {
                    helper.getView(R.id.iv_expand).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.iv_expand).setVisibility(View.GONE);
                }
                break;
            }
            //职业探索header
            case LAYOUT_OCCUPATION_EXPLORE_HEADER: {
                OccupationRecord entity = (OccupationRecord) item;
                if (entity.isFinish()) {
                    helper.getView(R.id.warpLinearLayout).setVisibility(View.VISIBLE);
                    WarpLinearLayout layout = helper.getView(R.id.warpLinearLayout);
                    if (layout.getChildCount() == 0) {
                        for (String str : entity.getCareer_areas()) {
                            TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.record_result_item, null);
                            tv.setText(str);
                            layout.addView(tv);
                        }
                    }
                } else {
                    helper.getView(R.id.warpLinearLayout).setVisibility(View.GONE);
                }
                break;
            }
            //职业探索item
            case LAYOUT_OCCUPATION_EXPLORE_ITEM: {
                OccupationRecordItem entity = (OccupationRecordItem) item;
                helper.getView(R.id.ll_result_common).setVisibility(View.GONE);
                helper.getView(R.id.ll_no_complete).setVisibility(View.GONE);

                helper.setText(R.id.tv_title, entity.getDimension_name());

                //是否完成
                if (entity.isFinish()) {
                    helper.getView(R.id.btn_report).setVisibility(View.VISIBLE);
                    helper.getView(R.id.ll_result_common).setVisibility(View.VISIBLE);
                    //推荐
                    List<String> result = entity.getResult();
                    if (ArrayListUtil.isNotEmpty(result)) {
                        helper.getView(R.id.warpLinearLayout).setVisibility(View.VISIBLE);
                        WarpLinearLayout layout = helper.getView(R.id.warpLinearLayout);
                        //结果数量小于等于视图数量
                        if (result.size() <= layout.getChildCount()) {
                            for (int i=0; i<layout.getChildCount(); i++) {
                                TextView textView = (TextView) layout.getChildAt(i);
                                if (i < result.size()) {
                                    textView.setVisibility(View.VISIBLE);
                                    textView.setText(result.get(i));
                                } else {
                                    textView.setVisibility(View.GONE);
                                }
                            }
                        } else {//结果数量大于视图数量
                            int childCount = layout.getChildCount();
                            for (int i=0; i<childCount; i++) {
                                TextView textView = (TextView) layout.getChildAt(i);
                                textView.setVisibility(View.VISIBLE);
                                textView.setText(result.get(i));
                            }

                            int addCount = result.size() - childCount;
                            for (int i=0; i<addCount; i++) {
                                TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.record_result_item, null);
                                tv.setText(result.get(childCount + i));
                                tv.setOnClickListener(new OnMultiClickListener() {
                                    @Override
                                    public void onMultiClick(View view) {
//                                        System.out.println(actType.getArea_name());
                                    }
                                });
                                layout.addView(tv);
                            }
                        }

                    } else {
                        helper.getView(R.id.warpLinearLayout).setVisibility(View.GONE);
                    }

                    //评价
                    if (!TextUtils.isEmpty(entity.getAppraisal())) {
                        helper.setText(R.id.tv_appraise, Html.fromHtml(entity.getAppraisal()));
                    } else {
                        helper.setText(R.id.tv_appraise, "暂无评价");
                    }

                } else {
                    helper.getView(R.id.btn_report).setVisibility(View.GONE);
                    helper.getView(R.id.ll_no_complete).setVisibility(View.VISIBLE);
                }
                break;
            }

            //简单量表
            case LAYOUT_SIMPLE_DIMENSION: {
                SimpleDimensionResult entity = (SimpleDimensionResult) item;
                if (entity.isFinish()) {
                    helper.getView(R.id.iv_report).setVisibility(View.VISIBLE);
                    String pre = entity.getDimension_name().length() > 2 ? entity.getDimension_name().substring(0, 2) : entity.getDimension_name();
                    helper.setText(R.id.tv_title, pre + entity.getAppraisal());

                } else {
                    helper.getView(R.id.iv_report).setVisibility(View.GONE);
                    helper.setText(R.id.tv_title, entity.getDimension_name());
                }
                break;
            }
        }
    }

}


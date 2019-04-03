package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.WarpLinearLayout;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.features_v2.entity.SimpleDimensionResult;
import com.cheersmind.cheersgenie.features_v2.entity.SysRmdCourse;
import com.cheersmind.cheersgenie.features_v2.entity.SysRmdCourseItem;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import java.util.List;

/**
 * 系统推荐选科recyclerView适配器
 */
public class SysRmdCourseRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    //系统推荐选科的header
    public static final int LAYOUT_SYS_RECOMMEND_HEADER = 1;
    //通用项
    public static final int LAYOUT_ITEM_COMMON = 2;
    //简单量表
    public static final int LAYOUT_SIMPLE_DIMENSION = 3;

    private Context context;

    public SysRmdCourseRecyclerAdapter(Context context, List<MultiItemEntity> data) {
        super(data);
        this.context = context;

        //系统推荐选科header
        addItemType(LAYOUT_SYS_RECOMMEND_HEADER, R.layout.recycleritem_sys_rmd_course_header);
        //系统推荐选科通用item
        addItemType(LAYOUT_ITEM_COMMON, R.layout.recycleritem_sys_rmd_common_item);
        //简单量表
        addItemType(LAYOUT_SIMPLE_DIMENSION, R.layout.recycleritem_career_record_simple_dimension);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            //系统推荐选科header
            case LAYOUT_SYS_RECOMMEND_HEADER: {
                SysRmdCourse entity = (SysRmdCourse) item;
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

                    //可报考专业百分比
                    if (entity.getRecommend_major_per() != null) {
                        helper.getView(R.id.tv_can_select_major_ratio).setVisibility(View.VISIBLE);
                        //可报考专业百分比
                        helper.setText(R.id.tv_can_select_major_ratio,
                                Html.fromHtml("-您可报考的专业占所有大学专业的<font color='" +
                                        context.getResources().getColor(R.color.colorAccent) +"'>" +
                                        entity.getRecommend_major_per() * 100 + "%</font>"));
                    } else {
                        helper.getView(R.id.tv_can_select_major_ratio).setVisibility(View.GONE);
                    }

                    //要求较高专业百分比
                    if (entity.getRecommend_high_major_per() != null) {
                        helper.getView(R.id.tv_high_require_major_ratio).setVisibility(View.VISIBLE);
                        //要求较高专业百分比
                        helper.setText(R.id.tv_high_require_major_ratio,
                                Html.fromHtml("-要求较高的专业占所有大学专业的<font color='" +
                                        context.getResources().getColor(R.color.colorAccent) +"'>" +
                                        entity.getRecommend_high_major_per() * 100 + "%</font>"));
                    } else {
                        helper.getView(R.id.tv_high_require_major_ratio).setVisibility(View.GONE);
                    }

                } else {
                    helper.getView(R.id.ll_result).setVisibility(View.GONE);
                }

                helper.addOnClickListener(R.id.btn_can_select_major);
                helper.addOnClickListener(R.id.btn_high_require_major);

                break;
            }
            //通用项
            case LAYOUT_ITEM_COMMON: {
                SysRmdCourseItem entity = (SysRmdCourseItem) item;
                helper.getView(R.id.ll_result_common).setVisibility(View.GONE);
                helper.getView(R.id.ll_no_complete).setVisibility(View.GONE);
                helper.getView(R.id.btn_report).setVisibility(View.GONE);
                //标题
                helper.setText(R.id.tv_title, (entity.getIndex() + 1) + "、" + entity.getDimension_name());
                //通用结果
                if (ArrayListUtil.isEmpty(entity.getDimensions())) {
                    //完成
                    if (entity.isFinish()) {
                        helper.getView(R.id.btn_report).setVisibility(View.VISIBLE);
                        helper.getView(R.id.ll_result_common).setVisibility(View.VISIBLE);

                        helper.getView(R.id.wllCourse).setVisibility(View.GONE);
                        helper.getView(R.id.wllOccupation).setVisibility(View.GONE);

                        //推荐课程
                        List<String> result = entity.getSubjects();
                        if (ArrayListUtil.isNotEmpty(result)) {
                            helper.getView(R.id.wllCourse).setVisibility(View.VISIBLE);
                            WarpLinearLayout layout = helper.getView(R.id.wllCourse);
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

                        }

                        //推荐职业
                        List<OccupationCategory> categories = entity.getAct_areas();
                        if (ArrayListUtil.isNotEmpty(categories)) {
                            helper.getView(R.id.wllOccupation).setVisibility(View.VISIBLE);
                            WarpLinearLayout layout = helper.getView(R.id.wllOccupation);
                            //结果数量小于等于视图数量
                            if (categories.size() <= layout.getChildCount()) {
                                for (int i=0; i<layout.getChildCount(); i++) {
                                    TextView textView = (TextView) layout.getChildAt(i);
                                    if (i < categories.size()) {
                                        textView.setVisibility(View.VISIBLE);
                                        final OccupationCategory category = categories.get(i);
                                        textView.setText(category.getArea_name());
                                        textView.setOnClickListener(new OnMultiClickListener() {
                                            @Override
                                            public void onMultiClick(View view) {
                                                if (actCategoryClickListener != null) {
                                                    actCategoryClickListener.onClick(category);
                                                }
                                            }
                                        });
                                    } else {
                                        textView.setVisibility(View.GONE);
                                    }
                                }
                            } else {//结果数量大于视图数量
                                int childCount = layout.getChildCount();
                                for (int i=0; i<childCount; i++) {
                                    TextView textView = (TextView) layout.getChildAt(i);
                                    textView.setVisibility(View.VISIBLE);
                                    final OccupationCategory category = categories.get(i);
                                    textView.setText(category.getArea_name());
                                    textView.setOnClickListener(new OnMultiClickListener() {
                                        @Override
                                        public void onMultiClick(View view) {
                                            if (actCategoryClickListener != null) {
                                                actCategoryClickListener.onClick(category);
                                            }
                                        }
                                    });
                                }

                                int addCount = categories.size() - childCount;
                                for (int i=0; i<addCount; i++) {
                                    TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.record_result_item, null);
                                    final OccupationCategory category = categories.get(childCount + i);
                                    textView.setText(category.getArea_name());
                                    textView.setOnClickListener(new OnMultiClickListener() {
                                        @Override
                                        public void onMultiClick(View view) {
                                            if (actCategoryClickListener != null) {
                                                actCategoryClickListener.onClick(category);
                                            }
                                        }
                                    });
                                    layout.addView(textView);
                                }
                            }

                        }

                    } else {//未完成
                        helper.getView(R.id.ll_no_complete).setVisibility(View.VISIBLE);
                    }
                }

                //分割线
//                if (entity.isLastInBrother()) {
//                    helper.getView(R.id.divider).setVisibility(View.GONE);
//                } else {
//                    helper.getView(R.id.divider).setVisibility(View.VISIBLE);
//                }

                helper.addOnClickListener(R.id.btn_report);
                helper.addOnClickListener(R.id.btn_to_exam);

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

    private OnActCategoryClickListener actCategoryClickListener;

    /**
     * ACT职业分类点击监听
     */
    public interface OnActCategoryClickListener {
        void onClick(OccupationCategory category);
    }

    public void setActCategoryClickListener(OnActCategoryClickListener actCategoryClickListener) {
        this.actCategoryClickListener = actCategoryClickListener;
    }

}


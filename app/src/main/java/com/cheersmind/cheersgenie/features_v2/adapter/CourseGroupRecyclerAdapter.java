package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.StringUtil;
import com.cheersmind.cheersgenie.features.view.WarpLinearLayout;
import com.cheersmind.cheersgenie.features_v2.entity.CourseGroup;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.facebook.drawee.view.SimpleDraweeView;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

/**
 * 学科组合recyclerView适配器
 */
public class CourseGroupRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    //可选选科
    public static final int LAYOUT_SELECT_CAN_SELECT = 1;
    //用户已选选科
    public static final int LAYOUT_SELECT_USER_HAS_SELECT = 2;

    //可控制是否四舍五入
    private NumberFormat nf;

    private Context context;

    //课程编码-名称
    private HashMap<String, String> courseNameMap = new HashMap<>();

    public CourseGroupRecyclerAdapter(Context context, List<MultiItemEntity> data) {
        super(data);
        this.context = context;

        //可选选科
        addItemType(LAYOUT_SELECT_CAN_SELECT, R.layout.recycleritem_course_group_can_select);
        //用户已选选科
        addItemType(LAYOUT_SELECT_USER_HAS_SELECT, R.layout.recycleritem_course_group_user_select);

        nf = NumberFormat.getNumberInstance();
        // 保留一位小数
        nf.setMaximumFractionDigits(1);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.DOWN);

        courseNameMap.put("1001", "语文");
        courseNameMap.put("1002", "数学");
        courseNameMap.put("1003", "英语");
        courseNameMap.put("1004", "物理");
        courseNameMap.put("1005", "化学");
        courseNameMap.put("1006", "生物");
        courseNameMap.put("1007", "历史");
        courseNameMap.put("1008", "地理");
        courseNameMap.put("1009", "政治");
        courseNameMap.put("1010", "信息");
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        CourseGroup entity = (CourseGroup) item;
        switch (helper.getItemViewType()) {
            //可选选科
            case LAYOUT_SELECT_CAN_SELECT: {
                helper.setText(R.id.tv_title, entity.getSubjectGroupName());
                helper.setText(R.id.tv_major_ratio, nf.format((entity.getRate() != null ? entity.getRate() : 0) * 100) + "%");
                helper.setText(R.id.tv_require_major_ratio, nf.format((entity.getRequireRate() != null ? entity.getRequireRate() : 0) * 100) + "%");
                helper.setText(R.id.tv_follow_major_ratio, nf.format((entity.getFollowRate() != null ? entity.getFollowRate() : 0) * 100) + "%");
                helper.setText(R.id.tv_ability_ratio, nf.format((entity.getAbility_rate() != null ? entity.getAbility_rate() : 0) * 100) + "%");

                //选中图标
                SimpleDraweeView ivSelect = helper.getView(R.id.iv_select);
                if (entity.isSelected()) {
                    ivSelect.setImageResource(R.drawable.check_box_outline);

                } else {
                    ivSelect.setImageResource(R.drawable.check_box_outline_bl);
                }

                //上次选中
                if (entity.isLastSelect()) {
                    helper.getView(R.id.vLastSelect).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.vLastSelect).setVisibility(View.GONE);
                }

                break;
            }
            //用户已选选科
            case LAYOUT_SELECT_USER_HAS_SELECT: {
                helper.getView(R.id.warpLinearLayout).setVisibility(View.GONE);

                helper.setText(R.id.tv_tag, "第" + (entity.getIndex() + 1) + "选择");
                helper.setText(R.id.tv_major_ratio, nf.format((entity.getRate() != null ? entity.getRate() : 0) * 100) + "%");
                helper.setText(R.id.tv_require_major_ratio, nf.format((entity.getRequireRate() != null ? entity.getRequireRate() : 0) * 100) + "%");
                helper.setText(R.id.tv_follow_major_ratio, nf.format((entity.getFollowRate() != null ? entity.getFollowRate() : 0) * 100) + "%");
                helper.setText(R.id.tv_ability_ratio, nf.format((entity.getAbility_rate() != null ? entity.getAbility_rate() : 0) * 100) + "%");

                //手选的科目
                if (!TextUtils.isEmpty(entity.getSubjectGroup())) {
                    String[] courses = StringUtil.stringSpilt(entity.getSubjectGroup(), 4);
                    if (courses.length > 0) {
                        helper.getView(R.id.warpLinearLayout).setVisibility(View.VISIBLE);
                        WarpLinearLayout layout = helper.getView(R.id.warpLinearLayout);
                        //结果数量小于等于视图数量
                        if (courses.length <= layout.getChildCount()) {
                            for (int i=0; i<layout.getChildCount(); i++) {
                                TextView textView = (TextView) layout.getChildAt(i);
                                if (i < courses.length) {
                                    textView.setVisibility(View.VISIBLE);
                                    textView.setText(courseNameMap.get(courses[i]));
                                } else {
                                    textView.setVisibility(View.GONE);
                                }
                            }
                        } else {//结果数量大于视图数量
                            int childCount = layout.getChildCount();
                            for (int i=0; i<childCount; i++) {
                                TextView textView = (TextView) layout.getChildAt(i);
                                textView.setVisibility(View.VISIBLE);
                                textView.setText(courseNameMap.get(courses[i]));
                            }

                            int addCount = courses.length - childCount;
                            for (int i=0; i<addCount; i++) {
                                TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.record_result_item_clickable, null);
                                textView.setText(courseNameMap.get(courses[childCount + i]));
                                layout.addView(textView);
                            }
                        }
                    }
                }

                //分割线
                if (entity.isLast()) {
                    helper.getView(R.id.divider).setVisibility(View.INVISIBLE);
                } else {
                    helper.getView(R.id.divider).setVisibility(View.VISIBLE);
                }

                break;
            }
        }
    }

}


package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专业观察表recycler适配器
 */
public class ObserveMajorRecyclerAdapter extends BaseQuickAdapter<RecommendMajor, BaseViewHolder> {

    private HashMap<String, String> courseNameMap;
    private boolean isCompleteSelect;//是否完成最终选科

    public ObserveMajorRecyclerAdapter(Context context, int layoutResId, @Nullable List<RecommendMajor> data,boolean isCompleteSelect) {
        super(layoutResId, data);

        this.isCompleteSelect = isCompleteSelect;
        initCourseMap();
    }

    /**
     * 初始化科目map
     */
    private void initCourseMap() {
        courseNameMap = new HashMap<>();
        courseNameMap.put("1001", "语");
        courseNameMap.put("1002", "数");
        courseNameMap.put("1003", "英");
        courseNameMap.put("1004", "物");
        courseNameMap.put("1005", "化");
        courseNameMap.put("1006", "生");
        courseNameMap.put("1007", "史");
        courseNameMap.put("1008", "地");
        courseNameMap.put("1009", "政");
        courseNameMap.put("1010", "技");
    }

    @Override
    protected void convert(BaseViewHolder helper, RecommendMajor item) {

        if(isCompleteSelect){
            helper.getView(R.id.iv_del).setVisibility(View.GONE);
            helper.getView(R.id.tv_title).setPadding(dip2px(mContext,15),0,0,0);
        }else{
            helper.getView(R.id.iv_del).setVisibility(View.VISIBLE);
        }

        //标题
        helper.setText(R.id.tv_title, item.getMajor_name());

        //第一组的学科
//        String requiredSubjects = item.getRequire_subjects();
//        if (TextUtils.isEmpty(requiredSubjects)) {
//            helper.setText(R.id.tv_course_group, "无限制");
//        } else {
//            String[] split = requiredSubjects.split("\\|\\|");
//            String res = generateFirstGroupCourseStr(split[0]);
//            helper.setText(R.id.tv_course_group, Html.fromHtml(res));
//        }

        //次数最多的三个学科
        String requiredSubjects = item.getRequire_subjects();
        if (TextUtils.isEmpty(requiredSubjects)) {
            helper.getView(R.id.ll_course).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.ll_course).setVisibility(View.VISIBLE);
            helper.getView(R.id.tv_course1).setVisibility(View.GONE);
            helper.getView(R.id.tv_course2).setVisibility(View.GONE);
            helper.getView(R.id.tv_course3).setVisibility(View.GONE);

            String[] split = requiredSubjects.split("\\|\\|");
            List<String> courseList = generateMaxCountCourse(split);
            //设置学科
            int courseLength = courseList.size();
            for (int i=0; i<courseLength; i++) {
                String course = courseList.get(i);

                if (i == 0) {
                    helper.getView(R.id.tv_course1).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_course1, course);
                } else if (i == 1) {
                    helper.getView(R.id.tv_course2).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_course2, course);
                } else if (i == 2) {
                    helper.getView(R.id.tv_course3).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_course3, course);
                }
            }
        }

        helper.addOnClickListener(R.id.iv_del);
    }

    /**
     * 次数最多的三个学科
     * @param group 科目组字符串
     * @return List<String>
     */
    private List<String> generateMaxCountCourse(String[] group) {
        List<String> resList = new ArrayList<>();

        if (group.length > 0) {
            HashMap<String, Integer> courseMap = new HashMap<>();
            for (String groupItem : group) {
                String[] items = groupItem.split(",");
                for (String item : items) {
                    String[] codes = item.split("\\|");
                    for (String code : codes) {
                        //累加
                        if (courseMap.containsKey(code)) {
                            courseMap.put(code, courseMap.get(code) + 1);

                        } else {
                            courseMap.put(code, 1);
                        }
                    }
                }
            }

            //选出最多的三个（目前先随便选三个）
            int count = 1;
            for (Map.Entry<String, Integer> entry : courseMap.entrySet()) {
                resList.add(courseNameMap.get(entry.getKey()));
                if (count == 3) {
                    break;
                }
                count++;
            }
        }

        return resList;
    }

    /**
     * 生成第一组要求科目的显示字符串
     * @param courseGroupStr 科目组字符串
     * @return 显示字符串
     */
    private String generateFirstGroupCourseStr(String courseGroupStr) {
        StringBuilder res = new StringBuilder();

        String[] split = courseGroupStr.split(",");
        if (split.length > 0) {
            for (String item : split) {
                String[] codes = item.split("\\|");
                if (codes.length == 1) {
                    if (res.length() > 0) {
                        res.append("、");
                    }
                    res.append(courseNameMap.get(codes[0]));
                } else {
                    if (res.length() > 0) {
                        res.append("、");
                    }
                    for (int i=0; i<codes.length; i++) {
                        String code = codes[i];
                        res.append(courseNameMap.get(code));
                        if (i < codes.length - 1) {
                            res.append("或");
                        }
                    }
                }
            }
        }

        return res.toString();
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 设置是否已经选科
     * @param completeSelect true:已经选科
     */
    public void setCompleteSelect(boolean completeSelect) {
        isCompleteSelect = completeSelect;
    }
}

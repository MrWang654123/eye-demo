package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;

import java.util.HashMap;
import java.util.List;

/**
 * 专业观察表recycler适配器
 */
public class ObserveMajorRecyclerAdapter extends BaseQuickAdapter<RecommendMajor, BaseViewHolder> {

    private HashMap<String, String> courseNameMap;

    public ObserveMajorRecyclerAdapter(Context context, int layoutResId, @Nullable List<RecommendMajor> data) {
        super(layoutResId, data);

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
        //标题
        helper.setText(R.id.tv_title, item.getMajor_name());

        //第一组的科目
        String requiredSubjects = item.getRequire_subjects();
        if (TextUtils.isEmpty(requiredSubjects)) {
            helper.setText(R.id.tv_course_group, "无限制");
        } else {
            String[] split = requiredSubjects.split("\\|\\|");
            String res = generateFirstGroupCourseStr(split[0]);
            helper.setText(R.id.tv_course_group, Html.fromHtml(res));
        }

        helper.addOnClickListener(R.id.iv_del);
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

}

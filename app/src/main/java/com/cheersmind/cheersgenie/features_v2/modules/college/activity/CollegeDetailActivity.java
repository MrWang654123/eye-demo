package com.cheersmind.cheersgenie.features_v2.modules.college.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.cheersmind.cheersgenie.features_v2.modules.base.activity.CommonAttentionActivity;
import com.cheersmind.cheersgenie.features_v2.modules.college.fragment.CollegeDetailFragment;

/**
 * 院校详情
 */
public class CollegeDetailActivity extends CommonAttentionActivity {

    /**
     * 启动院校详情页面
     *
     * @param context 上下文
     * @param college 院校
     */
    public static void startCollegeDetailActivity(Context context, CollegeEntity college) {
        Intent intent = new Intent(context, CollegeDetailActivity.class);
        intent.putExtra(DtoKey.COLLEGE, college);
        context.startActivity(intent);
    }

    @Override
    protected String settingTitle() {
        return "院校详情";
    }

    @Override
    protected void onInitData() {
        super.onInitData();
        //院校
        CollegeEntity college = (CollegeEntity) getIntent().getSerializableExtra(DtoKey.COLLEGE);

        dto.setEntity_id(college.getId());//ID
        dto.setTag(college.getCn_name());//名称
        dto.setType(Dictionary.ATTENTION_TYPE_COLLEGE);//对象类型

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = CollegeDetailFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //院校详情
            CollegeDetailFragment fragment = new CollegeDetailFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putSerializable(DtoKey.COLLEGE, college);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

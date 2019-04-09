package com.cheersmind.cheersgenie.features_v2.modules.major.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorItem;
import com.cheersmind.cheersgenie.features_v2.modules.base.activity.CommonAttentionActivity;
import com.cheersmind.cheersgenie.features_v2.modules.major.fragment.MajorDetailFragment;

/**
 * 专业详情
 */
public class MajorDetailActivity extends CommonAttentionActivity {

    /**
     * 启动专业详情页面
     * @param context 上下文
     * @param majorItem 专业
     */
    public static void startMajorDetailActivity(Context context, MajorItem majorItem) {
        Intent intent = new Intent(context, MajorDetailActivity.class);
        intent.putExtra(DtoKey.MAJOR, majorItem);
        context.startActivity(intent);
    }

    @Override
    protected String settingTitle() {
        return "专业详情";
    }

    @Override
    protected void onInitData() {
        super.onInitData();
        //专业
        MajorItem major = (MajorItem) getIntent().getSerializableExtra(DtoKey.MAJOR);

        dto.setEntity_id(major.getMajor_code());//ID
        dto.setTag(major.getMajor_name());//名称
        dto.setType(Dictionary.ATTENTION_TYPE_MAJOR);//对象类型

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = MajorDetailFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //专业详情
            MajorDetailFragment fragment = new MajorDetailFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putSerializable(DtoKey.MAJOR, major);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

}

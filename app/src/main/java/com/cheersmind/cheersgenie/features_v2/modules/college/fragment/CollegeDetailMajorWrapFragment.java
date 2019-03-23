package com.cheersmind.cheersgenie.features_v2.modules.college.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features_v2.modules.major.fragment.MajorListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 院校的开设专业tab
 */
public class CollegeDetailMajorWrapFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //院校ID
    private String collegeId;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected int setContentView() {
        return R.layout.fragment_major;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            collegeId = bundle.getString(DtoKey.COLLEGE_ID);
        }

    }

    @Override
    protected void lazyLoad() {
        if (tabs.getTabCount() == 0) {
            List<Pair<String, Fragment>> items = new ArrayList<>();
            //专业
            CollegeDetailMajorFragment fragment1 = new CollegeDetailMajorFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putString(DtoKey.COLLEGE_ID, collegeId);
            fragment1.setArguments(bundle1);

            //国家重点学科
            CollegeDetailKeySubjectFragment fragment2 = new CollegeDetailKeySubjectFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putString(DtoKey.COLLEGE_ID, collegeId);
            bundle2.putString(DtoKey.KEY_SUBJECT_TYPE, Dictionary.KEY_SUBJECT_TYPE_COUNTRY);
            fragment2.setArguments(bundle2);
            //一流学科
            CollegeDetailKeySubjectFragment fragment3 = new CollegeDetailKeySubjectFragment();
            Bundle bundle3 = new Bundle();
            bundle3.putString(DtoKey.COLLEGE_ID, collegeId);
            bundle3.putString(DtoKey.KEY_SUBJECT_TYPE, Dictionary.KEY_SUBJECT_TYPE_FIRST_RATE);
            fragment3.setArguments(bundle3);

            items.add(new Pair<String, Fragment>("开设专业", fragment1));
            items.add(new Pair<String, Fragment>("国家重点", fragment2));
            items.add(new Pair<String, Fragment>("一流学科", fragment3));

            viewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager(), items));
            viewPager.setOffscreenPageLimit(2);
            //标签绑定viewpager
            tabs.setupWithViewPager(viewPager);
            //改变tab下划线的宽度
//        TabLayoutUtil.setTabWidth(tabs, DensityUtil.dip2px(getContext(), 50));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}


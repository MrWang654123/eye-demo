package com.cheersmind.cheersgenie.features_v2.modules.major.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 专业
 */
public class MajorFragment extends LazyLoadFragment {

    Unbinder unbinder;

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

        List<Pair<String, Fragment>> items = new ArrayList<>();
        //本科专业
        MajorListFragment fragment1 = new MajorListFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt(DtoKey.MAJOR_TYPE, MajorListFragment.TYPE_UNDERGRADUATE_MAJOR);
        fragment1.setArguments(bundle1);
        //专科专业
        MajorListFragment fragment2 = new MajorListFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt(DtoKey.MAJOR_TYPE, MajorListFragment.TYPE_JUNIOR_MAJOR);
        fragment2.setArguments(bundle2);

        items.add(new Pair<String, Fragment>("本科", fragment1));
        items.add(new Pair<String, Fragment>("专科", fragment2));
        viewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager(), items));
        //标签绑定viewpager
        tabs.setupWithViewPager(viewPager);
        //改变tab下划线的宽度
//        TabLayoutUtil.setTabWidth(tabs, DensityUtil.dip2px(getContext(), 50));
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}


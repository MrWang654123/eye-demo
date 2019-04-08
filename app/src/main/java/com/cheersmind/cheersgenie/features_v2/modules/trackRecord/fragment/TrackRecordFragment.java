package com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 成长档案
 */
public class TrackRecordFragment extends LazyLoadFragment {

    Unbinder unbinder;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    @Override
    protected int setContentView() {
        return R.layout.fragment_track_record;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //主图
//        ivMain.setImageURI(examTask != null ? examTask.getArticleImg() : "");

        //获取数据
        Bundle bundle = getArguments();
        CareerPlanReportFragment fragment1 = new CareerPlanReportFragment();
        fragment1.setArguments(bundle);

        List<Pair<String, Fragment>> items = new ArrayList<>();
        items.add(new Pair<String, Fragment>("生涯发展档案", fragment1));
        items.add(new Pair<String, Fragment>("能力发展档案", new DevelopmentRecordFragment()));
        viewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager(), items));
        //标签绑定viewpager
        tabs.setupWithViewPager(viewPager);
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


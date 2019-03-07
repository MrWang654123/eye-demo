package com.cheersmind.cheersgenie.features_v2.modules.mine.fragment;

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
 * 奖励
 */
public class MineAwardFragment extends LazyLoadFragment {

    Unbinder unbinder;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected int setContentView() {
        return R.layout.fragment_award;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        List<Pair<String, Fragment>> items = new ArrayList<>();
        items.add(new Pair<String, Fragment>("我的勋章", new MineMedalFragment()));
        items.add(new Pair<String, Fragment>("我的积分", new MineIntegralFragment()));
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


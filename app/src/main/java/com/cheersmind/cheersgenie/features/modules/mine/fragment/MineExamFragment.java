package com.cheersmind.cheersgenie.features.modules.mine.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.HistoryExamFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.HistorySeminarFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 我的测评
 */
public class MineExamFragment extends LazyLoadFragment {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    //title、Fragment组合对象的集合
    private List<Pair<String, Fragment>> items;
    //ButterKnife解绑对象
    Unbinder unbinder;


    @Override
    protected int setContentView() {
        return R.layout.fragment_mine_exam;
    }

    @Override
    protected void onInitView(View contentView) {
        unbinder = ButterKnife.bind(this, contentView);

        items = new ArrayList<>();
        items.add(new Pair<String, Fragment>("全部", new HistoryExamFragment()));
        items.add(new Pair<String, Fragment>("专题", new HistorySeminarFragment()));
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

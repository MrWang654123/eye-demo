package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabViewPagerAdapter;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.ExamCompletedFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.ExamDoingFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 我的测评
 */
public class MineExamFragment extends LazyLoadFragment {
    //标签布局
    @BindView(R.id.tabs)
    TabLayout tabs;
    //内容viewpager
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    //ButterKnife解绑对象
    Unbinder unbinder;


    @Override
    protected int setContentView() {
        return R.layout.fragment_mine_exam;
    }

    @Override
    protected void onInitView(View contentView) {
        //ButterKnife绑定
        unbinder = ButterKnife.bind(this, contentView);

        List<Pair<String, Fragment>> items = new ArrayList<>();
        items.add(new Pair<String, Fragment>("正在测评", new ExamDoingFragment()));
        items.add(new Pair<String, Fragment>("已完成", new ExamCompletedFragment()));
        viewPager.setAdapter(new TabViewPagerAdapter(getChildFragmentManager(), items));
        //标签绑定viewpager
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void lazyLoad() {

    }

    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

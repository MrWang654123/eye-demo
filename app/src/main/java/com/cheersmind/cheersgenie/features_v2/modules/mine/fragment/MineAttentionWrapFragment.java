package com.cheersmind.cheersgenie.features_v2.modules.mine.fragment;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 我的关注包裹页面
 */
public class MineAttentionWrapFragment extends LazyLoadFragment {

    Unbinder unbinder;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected int setContentView() {
        return R.layout.fragment_mine_attention_wrap;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        List<Pair<String, Fragment>> items = new ArrayList<>();
        MineAttentionFragment fragment1 = new MineAttentionFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt(DtoKey.ATTENTION_TYPE, Dictionary.ATTENTION_TYPE_COLLEGE);
        fragment1.setArguments(bundle1);

        MineAttentionFragment fragment2 = new MineAttentionFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt(DtoKey.ATTENTION_TYPE, Dictionary.ATTENTION_TYPE_MAJOR);
        fragment2.setArguments(bundle2);

        MineAttentionFragment fragment3 = new MineAttentionFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt(DtoKey.ATTENTION_TYPE, Dictionary.ATTENTION_TYPE_OCCUPATION);
        fragment3.setArguments(bundle3);

        items.add(new Pair<String, Fragment>("院校", fragment1));
        items.add(new Pair<String, Fragment>("专业", fragment2));
        items.add(new Pair<String, Fragment>("职业", fragment3));

        viewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager(), items));
        viewPager.setOffscreenPageLimit(2);
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


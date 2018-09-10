package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.modules.message.fragment.SystemMessageFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 消息页面（包括公告、提醒）
 */
public class MessageFragment extends LazyLoadFragment {
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;

    //title、Fragment组合对象的集合
    private List<Pair<String, Fragment>> items;

    @Override
    protected int setContentView() {
        return R.layout.fragment_message;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        items = new ArrayList<>();
        items.add(new Pair<String, Fragment>("通知", new SystemMessageFragment()));
//        items.add(new Pair<String, Fragment>("提醒", new PersonalMessageFragment()));
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


package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features_v2.event.AddObserveMajorEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 选科助手
 */
public class SelectCourseAssistantFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //孩子测评ID
    private String childExamId;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_select_course_assistant;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            childExamId = bundle.getString(DtoKey.CHILD_EXAM_ID);
        }

        List<Pair<String, Fragment>> items = new ArrayList<>();
        //系统推荐
        SysRecommendCourseFragment fragment1 = new SysRecommendCourseFragment();
        fragment1.setArguments(bundle);
        //专业观察表
        ObserveMajorFragment fragment2 = new ObserveMajorFragment();
        fragment2.setArguments(bundle);
        //选科确认
        ChooseCourseFragment fragment3 = new ChooseCourseFragment();
        fragment3.setArguments(bundle);

        items.add(new Pair<String, Fragment>("系统推荐", fragment1));
        items.add(new Pair<String, Fragment>("专业观察表", fragment2));
        items.add(new Pair<String, Fragment>("选科确认", fragment3));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }

    /**
     * 添加观察专业的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddObserveMajorNotice(AddObserveMajorEvent event) {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem != 1) {
            viewPager.setCurrentItem(1);
        }
    }

}


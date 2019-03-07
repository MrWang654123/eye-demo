package com.cheersmind.cheersgenie.features_v2.modules.major.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorEntity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskCommentFragment;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskItemFragment;
import com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment.CareerPlanReportFragment;
import com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment.TrackRecordDetailFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 专业详情
 */
public class MajorDetailFragment extends LazyLoadFragment {

    Unbinder unbinder;
    //专业
    private MajorEntity major;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    @Override
    protected int setContentView() {
        return R.layout.fragment_major_detail;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            major = (MajorEntity) bundle.getSerializable(DtoKey.MAJOR);
        }

        List<Pair<String, Fragment>> items = new ArrayList<>();
        MajorDetailInfoFragment fragment1 = new MajorDetailInfoFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable(DtoKey.MAJOR_ID, major.getId());
        fragment1.setArguments(bundle);
        MajorDetailCollegeFragment fragment2 = new MajorDetailCollegeFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable(DtoKey.MAJOR_ID, major.getId());
        fragment2.setArguments(bundle);
        items.add(new Pair<String, Fragment>("基本信息", fragment1));
        items.add(new Pair<String, Fragment>("开设院校", fragment2));
        viewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager(), items));
        //标签绑定viewpager
        tabs.setupWithViewPager(viewPager);

        //监听 AppBarLayout Offset 变化，动态设置 SwipeRefreshLayout 是否可用
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    //发送停止Fling的事件
                    EventBus.getDefault().post(new StopFlingEvent());
                }

            }
        });

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


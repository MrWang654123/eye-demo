package com.cheersmind.cheersgenie.features_v2.modules.college.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 院校详情
 */
public class CollegeDetailFragment extends LazyLoadFragment {

    Unbinder unbinder;
    //院校
    private CollegeEntity college;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    @BindView(R.id.iv_main)
    SimpleDraweeView ivMain;
    @BindView(R.id.tv_college_name)
    TextView tvCollegeName;


    @Override
    protected int setContentView() {
        return R.layout.fragment_college_detail;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            college = (CollegeEntity) bundle.getSerializable(DtoKey.COLLEGE);
        }

        List<Pair<String, Fragment>> items = new ArrayList<>();
        CollegeDetailInfoFragment fragment1 = new CollegeDetailInfoFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable(DtoKey.COLLEGE_ID, college.getId());
        fragment1.setArguments(bundle);
        CollegeDetailEnrollFragment fragment2 = new CollegeDetailEnrollFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable(DtoKey.COLLEGE_ID, college.getId());
        fragment2.setArguments(bundle);
        items.add(new Pair<String, Fragment>("简介", fragment1));
        items.add(new Pair<String, Fragment>("录取", fragment2));
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

        //基本信息
        ivMain.setImageURI(college.getArticleImg());
        tvCollegeName.setText(college.getArticleTitle().length() > 5 ? college.getArticleTitle().substring(0, 5) : college.getArticleTitle());
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


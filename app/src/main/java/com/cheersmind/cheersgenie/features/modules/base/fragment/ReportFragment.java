package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabViewPagerAdapter;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.ExamCompletedFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.ExamDoingFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 报告主页面
 */
public class ReportFragment extends LazyLoadFragment {

    @Override
    protected int setContentView() {
        return R.layout.fragment_report;
    }

    @Override
    protected void onInitView(View contentView) {
    }

    @Override
    protected void lazyLoad() {

    }

}

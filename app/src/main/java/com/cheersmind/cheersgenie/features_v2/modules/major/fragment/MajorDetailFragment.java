package com.cheersmind.cheersgenie.features_v2.modules.major.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.entity.MajorDetail;
import com.cheersmind.cheersgenie.features_v2.entity.MajorItem;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 专业详情
 */
public class MajorDetailFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //专业
    private MajorItem major;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    //专业名称
    @BindView(R.id.tv_major_name)
    TextView tvMajorName;
    //专业代码
    @BindView(R.id.tv_major_code)
    TextView tvMajorCode;
    //学位
    @BindView(R.id.tv_degree_val)
    TextView tvDegreeVal;
    //学制
    @BindView(R.id.tv_duration_val)
    TextView tvDurationVal;
    //所属学科
    @BindView(R.id.tv_subject_val)
    TextView tvSubjectVal;
    //所属门类
    @BindView(R.id.tv_category_val)
    TextView tvCategoryVal;

    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

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
            major = (MajorItem) bundle.getSerializable(DtoKey.MAJOR);
        }

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

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_major_detail_info));
        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //初始化为加载状态
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
                //加载专业详情
                if (major != null && !TextUtils.isEmpty(major.getMajor_code())) {
                    loadMajorDetail(major.getMajor_code());
                }
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

    }

    @Override
    protected void lazyLoad() {
        //加载专业详情
        if (major != null && !TextUtils.isEmpty(major.getMajor_code())) {
            loadMajorDetail(major.getMajor_code());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 加载专业详情
     *
     * @param majorCode 专业代码
     */
    private void loadMajorDetail(String majorCode) {

        DataRequestService.getInstance().getMajorDetail(majorCode, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    final MajorDetail majorDetail = InjectionWrapperUtil.injectMap(dataMap, MajorDetail.class);

                    //设置基本信息
                    settingBaseInfo(majorDetail);
                    //生成tabs
                    generateTabs(majorDetail);

                } catch (Exception e) {
                    e.printStackTrace();
                    //视为找不到文章数据
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }
            }
        }, httpTag, getContext());
    }

    /**
     * 设置基本信息
     * @param majorDetail 专业详情
     */
    private void settingBaseInfo(MajorDetail majorDetail) {
        tvMajorName.setText(majorDetail.getMajor_name());
        String majorCode = "[" + majorDetail.getMajor_code() + "]";
        tvMajorCode.setText(majorCode);
        tvDegreeVal.setText(majorDetail.getDegree());
        String durationStr = "";
        if (majorDetail.getLearn_year() > 0) {
            durationStr = majorDetail.getLearn_year() + "年";
        }
        tvDurationVal.setText(durationStr);
        tvSubjectVal.setText(majorDetail.getSubject());
        tvCategoryVal.setText(majorDetail.getCategory());
    }

    /**
     * 生成tabs
     * @param majorDetail 专业详情
     */
    private void generateTabs(MajorDetail majorDetail) {
        List<Pair<String, Fragment>> items = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DtoKey.MAJOR_DETAIL, majorDetail);

        MajorDetailInfoFragment fragment1 = new MajorDetailInfoFragment();
        fragment1.setArguments(bundle);

        MajorDetailCollegeFragment fragment2 = new MajorDetailCollegeFragment();
        fragment2.setArguments(bundle);

        MajorEmploymentFragment fragment3 = new MajorEmploymentFragment();
        fragment3.setArguments(bundle);

        items.add(new Pair<String, Fragment>("基本信息", fragment1));
        items.add(new Pair<String, Fragment>("开设院校", fragment2));
        items.add(new Pair<String, Fragment>("就业前景", fragment3));
        viewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager(), items));
        viewPager.setOffscreenPageLimit(2);
        //标签绑定viewpager
        tabs.setupWithViewPager(viewPager);
    }

}


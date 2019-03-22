package com.cheersmind.cheersgenie.features_v2.modules.major.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.entity.MajorDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 专业就业前景
 */
public class MajorEmploymentFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //专业详情
    private MajorDetail majorDetail;

    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    @BindView(R.id.nsv_main)
    NestedScrollView nsvMain;

    @BindView(R.id.ll_employment_direction)
    LinearLayout llEmploymentDirection;
    //就业方向
    @BindView(R.id.tv_employment_direction)
    TextView tv_employment_direction;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_major_detail_employment;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            majorDetail = (MajorDetail) bundle.getSerializable(DtoKey.MAJOR_DETAIL);
        }

        initView(majorDetail);
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
     * 停止Fling的消息
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopFlingNotice(StopFlingEvent event) {
        if (nsvMain != null) {
            nsvMain.stopNestedScroll();
            nsvMain.stopNestedScroll(1);
            System.out.println("就业方向nsvMain.stopNestedScroll(1);");
        }
    }

    /**
     * 初始化就业前景
     *
     * @param majorDetail 专业详情
     */
    private void initView(MajorDetail majorDetail) {
        if (majorDetail == null) return;

        if (!TextUtils.isEmpty(majorDetail.getEmployment())) {
            tv_employment_direction.setText(majorDetail.getEmployment());

        } else {
            llEmploymentDirection.setVisibility(View.GONE);
            //空布局：无数据提示
            emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
        }
    }

}


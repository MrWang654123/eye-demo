package com.cheersmind.cheersgenie.features_v2.modules.major.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.MajorSuitOccupationRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.entity.MajorDetail;
import com.cheersmind.cheersgenie.features_v2.entity.MajorIntroduce;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationItem;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.activity.OccupationDetailActivity;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 专业详情基本信息
 */
public class MajorDetailInfoFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //专业详情
    private MajorDetail majorDetail;

    @BindView(R.id.nsv_main)
    NestedScrollView nsvMain;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;
    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    @BindView(R.id.ll_introduce)
    LinearLayout llIntroduce;
    @BindView(R.id.ll_course)
    LinearLayout llCourse;
    @BindView(R.id.ll_suit_occupation)
    LinearLayout llSuitOccupation;

    //可伸缩文本
    @BindView(R.id.expand_text_view)
    ExpandableTextView expandableTextView;

    //课程
    @BindView(R.id.tv_course)
    TextView tvCourse;

    //对口职业列表
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    MajorSuitOccupationRecyclerAdapter recyclerAdapter;

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            OccupationItem entity = recyclerAdapter.getData().get(position);
            OccupationDetailActivity.startOccupationDetailActivity(getContext(), entity);
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_major_detail_info;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            majorDetail = (MajorDetail) bundle.getSerializable(DtoKey.MAJOR_DETAIL);
        }

//        //设置无数据提示文本
//        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_major_detail_info));
//        //重载监听
//        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
//            @Override
//            public void onMultiClick(View view) {
//                //加载数据
//                loadData();
//            }
//        });
//        //初始化为加载状态
//        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        //设置recyclerView不影响嵌套滚动
        recyclerView.setNestedScrollingEnabled(false);
        //使其失去焦点。
        recyclerView.setFocusable(false);

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
//            nsvMain.stopNestedScroll();
            nsvMain.stopNestedScroll(1);
        }
    }

    /**
     * 初始化专业详情
     *
     * @param majorDetail 专业详情
     */
    private void initView(MajorDetail majorDetail) {
        if (majorDetail == null) return;

        //简介
        List<MajorIntroduce> introduces = majorDetail.getIntroduces();
        if (ArrayListUtil.isNotEmpty(introduces)) {
            StringBuilder builder = new StringBuilder();
            for (int i=0; i<introduces.size(); i++) {
                MajorIntroduce introduce = introduces.get(i);

                builder.append("<b>");
                builder.append(introduce.getTitle());
                builder.append("</b><br/>");
                builder.append(introduce.getContent());
                if (i != introduces.size() - 1) {
                    builder.append("<br/>");
                }
            }

            if (builder.length() > 0) {
                expandableTextView.setText(Html.fromHtml(builder.toString()));
            }
        } else {
            llIntroduce.setVisibility(View.GONE);
        }

        //课程概览
        if (!TextUtils.isEmpty(majorDetail.getCourse())) {
            tvCourse.setText(majorDetail.getCourse());
        } else {
            llCourse.setVisibility(View.GONE);
        }

        //对口职业
        if (ArrayListUtil.isNotEmpty(majorDetail.getSuitOccupations())) {
            recyclerAdapter = new MajorSuitOccupationRecyclerAdapter(
                    R.layout.recycleritem_major_suit_occupation, majorDetail.getSuitOccupations());
            recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(recyclerAdapter);

        } else {
            llSuitOccupation.setVisibility(View.GONE);
        }
    }

}


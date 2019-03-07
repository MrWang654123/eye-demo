package com.cheersmind.cheersgenie.features_v2.modules.college.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.entity.TrackRecordEntity;
import com.cheersmind.cheersgenie.features_v2.entity.TrackRecordRootEntity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 院校录取信息
 */
public class CollegeDetailEnrollFragment extends LazyLoadFragment {

    Unbinder unbinder;
    //院校ID
    private String collegeId;

    @BindView(R.id.nsv_main)
    NestedScrollView nsvMain;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;
    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    ArticleDto dto;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_college_detail_enroll;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            collegeId = bundle.getString(DtoKey.COLLEGE_ID);
        }

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_college_detail_info));
        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载数据
                loadData();
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        dto = new ArticleDto(pageNum, PAGE_SIZE);
    }

    @Override
    protected void lazyLoad() {
        //加载数据
        loadData();
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
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopFlingNotice(StopFlingEvent event) {
        if (nsvMain != null) {
//            nsvMain.stopNestedScroll();
            nsvMain.stopNestedScroll(1);
        }
    }


    /**
     * 加载数据
     */
    private void loadData() {
        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        try {
            dto.setPage(pageNum);
            DataRequestService.getInstance().getArticles(dto, new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    //空布局：网络连接有误，或者请求失败
                    emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                }

                @Override
                public void onResponse(Object obj) {
                    //空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    try {
                        Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                        TrackRecordRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, TrackRecordRootEntity.class);

                        totalCount = rootEntity.getTotal();
                        List<TrackRecordEntity> dataList = rootEntity.getItems();

                        //空数据处理
                        if (ArrayListUtil.isEmpty(dataList)) {
                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                            return;
                        }

                        //页码+1
                        pageNum++;

                    } catch (Exception e) {
                        e.printStackTrace();
                        //空布局：加载失败
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    }
                }
            }, httpTag, getActivity());

        } catch (Exception e) {
            e.printStackTrace();
            //空布局：网络连接有误，或者请求失败
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
        }
    }


}


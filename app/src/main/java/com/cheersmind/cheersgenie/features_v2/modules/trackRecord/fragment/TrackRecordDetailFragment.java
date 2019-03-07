package com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.ReportMultiRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.ExamTaskRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.TrackRecordRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.TrackRecordEntity;
import com.cheersmind.cheersgenie.features_v2.entity.TrackRecordRootEntity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportResultEntity;
import com.cheersmind.cheersgenie.main.entity.ReportRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 成长档案明细
 */
public class TrackRecordDetailFragment extends LazyLoadFragment {

    Unbinder unbinder;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //适配器的数据列表
    TrackRecordRecyclerAdapter recyclerAdapter;

    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载数据
            loadData();
        }
    };

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
        return R.layout.fragment_track_record_detail;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        recyclerAdapter = new TrackRecordRecyclerAdapter(null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        //设置上拉加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(loadMoreListener, recycleView);
        //禁用未满页自动触发上拉加载
        recyclerAdapter.disableLoadMoreIfNotFullPage();
        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());
        //预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        recyclerAdapter.setPreLoadNumber(4);
        //添加一个空HeaderView，用于显示顶部分割线
        recyclerAdapter.addHeaderView(new View(getContext()));
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_custom));
        recycleView.addItemDecoration(divider);

        //滑动监听
        try {
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_track_record));
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
        if (recycleView != null) {
            recycleView.stopScroll();
        }

    }


    /**
     * 加载数据
     */
    private void loadData() {
        //通信等待提示
//        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

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

                        //测试处理
                        for (TrackRecordEntity entity : dataList) {
                            if (TextUtils.isEmpty(entity.getSummary()) || entity.getSummary().length() < 20) {
                                entity.setItemType(TrackRecordEntity.TYPE_NO_DATA_RECOMMEND);
                            }
                        }

                        //当前列表无数据
                        if (recyclerAdapter.getData().size() == 0) {
                            recyclerAdapter.setNewData(dataList);

                        } else {
                            recyclerAdapter.addData(dataList);
                        }

                        //判断是否全部加载结束
                        if (recyclerAdapter.getData().size() >= totalCount) {
                            //全部加载结束
                            recyclerAdapter.loadMoreEnd();
                        } else {
                            //本次加载完成
                            recyclerAdapter.loadMoreComplete();
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


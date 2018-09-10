package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.MineIntegralRecyclerAdapter;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.IntegralEntity;
import com.cheersmind.cheersgenie.main.entity.IntegralRootEntity;
import com.cheersmind.cheersgenie.main.entity.TopicRootEntity;
import com.cheersmind.cheersgenie.main.entity.TotalIntegralEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 我的积分
 */
public class MineIntegralActivity extends BaseActivity {
    ////总积分的布局
    @BindView(R.id.rl_total_integral)
    RelativeLayout rlTotalIntegral;
    @BindView(R.id.tv_has_integral)
    TextView tvHasIntegral;
    @BindView(R.id.tv_usable_integral)
    TextView tvUsableIntegral;
    //积分列表
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //下拉刷新视图
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    //适配器的数据列表
    List<IntegralEntity> recyclerItem;
    //适配器
    MineIntegralRecyclerAdapter recyclerAdapter;
    //空布局
    XEmptyLayout xemptyLayout;

    //下拉刷新的监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //加载总积分
            loadIntegralTotalScore();
            //加载积分数据
            loadIntegralData();
        }
    };
    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载积分数据
            loadIntegralData();
        }
    };

    //页长度
    private static final int PAGE_SIZE = 12;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    @Override
    protected int setContentView() {
        return R.layout.activity_mine_integral;
    }

    @Override
    protected String settingTitle() {
        return "我的积分";
    }

    @Override
    protected void onInitView() {

        recyclerAdapter = new MineIntegralRecyclerAdapter(R.layout.recycleritem_mine_integral, recyclerItem);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        //设置上拉加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(loadMoreListener, recycleView);
        //禁用未满页自动触发上拉加载
        recyclerAdapter.disableLoadMoreIfNotFullPage();
        //设置空数据布局
        View view = getLayoutInflater().inflate(R.layout.layout_xempty_recycler, (ViewGroup) recycleView.getParent(), false);
        xemptyLayout = view.findViewById(R.id.xemptyLayout);
        recyclerAdapter.setEmptyView(view);
        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());
        recycleView.setLayoutManager(new LinearLayoutManager(MineIntegralActivity.this));
        recycleView.setAdapter(recyclerAdapter);
        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        //初始隐藏总积分布局
        rlTotalIntegral.setVisibility(View.GONE);
    }

    @Override
    protected void onInitData() {
        //加载总积分
        loadIntegralTotalScore();
        //加载积分
        loadIntegralData();
    }

    /**
     * 加载总积分
     */
    private void loadIntegralTotalScore() {
        DataRequestService.getInstance().getIntegralTotalScore(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
                //刷新总积分视图
                refreshIntegralTotalScoreView(new TotalIntegralEntity());
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    TotalIntegralEntity totalIntegralEntity = InjectionWrapperUtil.injectMap(dataMap, TotalIntegralEntity.class);
                    //刷新总积分视图
                    refreshIntegralTotalScoreView(totalIntegralEntity);

                } catch (Exception e) {
                    e.printStackTrace();
                    //刷新总积分视图
                    refreshIntegralTotalScoreView(new TotalIntegralEntity());
                }
            }
        });
    }

    /**
     * 刷新总积分视图
     * @param totalIntegralEntity 总积分对象
     */
    private void refreshIntegralTotalScoreView(TotalIntegralEntity totalIntegralEntity) {
        //总积分的布局
        rlTotalIntegral.setVisibility(View.VISIBLE);
        //总积分
        tvHasIntegral.setText(getResources().getString(R.string.total_integral,totalIntegralEntity.getTotal() + ""));
        //可用积分
        tvUsableIntegral.setText(getResources().getString(R.string.available_integral, totalIntegralEntity.getConsumable() + ""));
    }

    /**
     * 加载积分数据
     */
    private void loadIntegralData() {
        //设置空布局，当前列表还没有数据的情况，提示：通信等待提示中
        if (ArrayListUtil.isEmpty(recyclerItem)) {
            xemptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }
        if (swipeRefreshLayout.isRefreshing()) {
            //下拉刷新
            pageNum = 1;
            recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        }

        DataRequestService.getInstance().getIntegralList(pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                if (swipeRefreshLayout.isRefreshing()) {
                    //下拉刷新
                    //开启上拉加载功能
                    recyclerAdapter.setEnableLoadMore(true);
                    //结束下拉刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    //上拉加载
                    if (ArrayListUtil.isNotEmpty(recyclerItem)) {
                        //加载失败处理
                        recyclerAdapter.loadMoreFail();
                    }
                }

                //设置空布局：当前列表还没有数据的情况，提示：网络连接有误，或者请求失败
                if (ArrayListUtil.isEmpty(recyclerItem)) {
                    xemptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                }
            }

            @Override
            public void onResponse(Object obj) {
                //设置空布局
//                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    IntegralRootEntity integralRoot = InjectionWrapperUtil.injectMap(dataMap, IntegralRootEntity.class);
                    //非空
                    if (ArrayListUtil.isEmpty(integralRoot.getItems())) {
                        throw new Exception();
                    }

                    //后台总记录数
                    totalCount = integralRoot.getTotal();
                    List<IntegralEntity> dataList = integralRoot.getItems();

                    if (swipeRefreshLayout.isRefreshing()) {
                        //下拉刷新
                        recyclerItem = dataList;
                        recyclerAdapter.setNewData(recyclerItem);
                        //判断是否全部加载结束
                        if (recyclerItem.size() >= totalCount) {
                            //全部加载结束
                            recyclerAdapter.loadMoreEnd();
                        }

                    } else {
                        //上拉加载
                        if (ArrayListUtil.isEmpty(recyclerItem)) {
                            recyclerItem = dataList;
                            recyclerAdapter.setNewData(recyclerItem);
                        } else {
//                            recyclerItem.addAll(dataList);
                            //dataList会被添加到recyclerItem中
                            recyclerAdapter.addData(dataList);
                        }

                        //判断是否全部加载结束
                        if (recyclerItem.size() >= totalCount) {
                            //全部加载结束
                            recyclerAdapter.loadMoreEnd();
                        } else {
                            //本次加载完成
                            recyclerAdapter.loadMoreComplete();
                        }
                    }
                    //页码+1
                    pageNum++;

                } catch (Exception e) {
                    e.printStackTrace();
                    //列表中已经加载成功过数据，本次加载数据为0条，则认为已经全部加载结束
                    if (recyclerAdapter.getItemCount() > 0) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd();
                    } else {
                        //无数据情况处理
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                        recyclerAdapter.setNewData(null);
                    }

                    //设置空布局，当前列表还没有数据的情况，提示：没有数据
//                    if (ArrayListUtil.isEmpty(topicList)) {
//                        xemptyLayout.setErrorType(XEmptyLayout.NODATA);
//                    }
                    //设置空布局：当前列表还没有数据的情况，提示：没有数据
                    if (ArrayListUtil.isEmpty(recyclerItem)) {
                        xemptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                    }
                }

                //还原动画
                if (swipeRefreshLayout.isRefreshing()) {
                    //下拉刷新
                    //开启上拉加载功能
                    recyclerAdapter.setEnableLoadMore(true);
                    //结束下拉刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

}



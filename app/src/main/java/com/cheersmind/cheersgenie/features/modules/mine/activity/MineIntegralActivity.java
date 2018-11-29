package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.MineIntegralRecyclerAdapter;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.IntegralEntity;
import com.cheersmind.cheersgenie.main.entity.IntegralRootEntity;
import com.cheersmind.cheersgenie.main.entity.TotalIntegralEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的积分
 */
public class MineIntegralActivity extends BaseActivity {
    ////总积分的布局
    @BindView(R.id.rl_total_integral)
    RelativeLayout rlTotalIntegral;
    @BindView(R.id.tv_has_integral_val)
    TextView tvHasIntegralVal;
    @BindView(R.id.tv_usable_integral_val)
    TextView tvUsableIntegralVal;
    //积分列表
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //下拉刷新视图
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //适配器的数据列表
//    List<IntegralEntity> recyclerItem;
    //适配器
    MineIntegralRecyclerAdapter recyclerAdapter;


    //下拉刷新的监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //加载总积分
            loadIntegralTotalScore();
            //刷新积分数据
            refreshIntegralData();
        }
    };
    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多积分
            loadMoreIntegralData();
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

    @SuppressLint("RestrictedApi")
    @Override
    protected void onInitView() {

        recyclerAdapter = new MineIntegralRecyclerAdapter(R.layout.recycleritem_mine_integral, null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        //设置上拉加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(loadMoreListener, recycleView);
        //禁用未满页自动触发上拉加载
        recyclerAdapter.disableLoadMoreIfNotFullPage();
        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());
        //预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        recyclerAdapter.setPreLoadNumber(4);
        recycleView.setLayoutManager(new LinearLayoutManager(MineIntegralActivity.this));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(MineIntegralActivity.this,DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(MineIntegralActivity.this,R.drawable.recycler_divider_line_f5f5f5));
//        recycleView.addItemDecoration(divider);
        //滑动监听
        try {
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(MineIntegralActivity.this, fabGotoTop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        //设置样式刷新显示的位置
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_integral));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载总积分
                loadIntegralTotalScore();
                //加载更多积分
                loadMoreIntegralData();
            }
        });

        //初始隐藏总积分布局
        rlTotalIntegral.setVisibility(View.GONE);
        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onInitData() {
        //加载总积分
        loadIntegralTotalScore();
        //加载更多积分
        loadMoreIntegralData();
    }


    @OnClick({R.id.tv_integral_desc})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //积分说明
            case R.id.tv_integral_desc: {
                startActivity(new Intent(MineIntegralActivity.this, IntegralDescActivity.class));
                break;
            }
        }
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
        tvHasIntegralVal.setText(totalIntegralEntity.getTotal() + "");
        //可用积分
        tvUsableIntegralVal.setText(totalIntegralEntity.getConsumable() + "");
    }

    /**
     * 刷新积分列表
     */
    private void refreshIntegralData() {
        //下拉刷新
        pageNum = 1;
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        DataRequestService.getInstance().getIntegralList(pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //开启上拉加载功能
                recyclerAdapter.setEnableLoadMore(true);
                //结束下拉刷新动画
                swipeRefreshLayout.setRefreshing(false);
                //设置空布局：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                //清空列表数据
                recyclerAdapter.setNewData(null);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //开启上拉加载功能
                    recyclerAdapter.setEnableLoadMore(true);
                    //结束下拉刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                    //设置空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    IntegralRootEntity integralRoot = InjectionWrapperUtil.injectMap(dataMap, IntegralRootEntity.class);

                    totalCount = integralRoot.getTotal();
                    List<IntegralEntity> dataList = integralRoot.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //下拉刷新
                    recyclerAdapter.setNewData(dataList);
                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        if (recyclerAdapter.getData().size() < 20) {
                            recyclerAdapter.loadMoreEnd(true);
                        } else {
                            recyclerAdapter.loadMoreEnd(false);
                        }
                    } else {
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                    }

                    //页码+1
                    pageNum++;

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    //清空列表数据
                    recyclerAdapter.setNewData(null);
                }

            }
        });
    }

    /**
     * 加载更多积分数据
     */
    private void loadMoreIntegralData() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        DataRequestService.getInstance().getIntegralList(pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //开启下拉刷新功能
                swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突

                if (recyclerAdapter.getData().size() == 0) {
                    //设置空布局：网络错误
                    emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                } else {
                    //加载失败处理
                    recyclerAdapter.loadMoreFail();
                }
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //开启下拉刷新功能
                    swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突
                    //设置空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    IntegralRootEntity integralRoot = InjectionWrapperUtil.injectMap(dataMap, IntegralRootEntity.class);

                    totalCount = integralRoot.getTotal();
                    List<IntegralEntity> dataList = integralRoot.getItems();
//                    IntegralEntity integralEntity = dataList.get(0);
//                    for (int i=0; i<20; i++) {
//                        dataList.add(integralEntity);
//                    }

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
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
                        if (recyclerAdapter.getData().size() < 20) {
                            recyclerAdapter.loadMoreEnd(true);
                        } else {
                            recyclerAdapter.loadMoreEnd(false);
                        }
                    } else {
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                    }

                    //页码+1
                    pageNum++;

                } catch (Exception e) {
                    e.printStackTrace();
                    if (recyclerAdapter.getData().size() == 0) {
                        //设置空布局：没有数据，可重载
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    } else {
                        //加载失败处理
                        recyclerAdapter.loadMoreFail();
                    }
                }

            }
        });
    }

}



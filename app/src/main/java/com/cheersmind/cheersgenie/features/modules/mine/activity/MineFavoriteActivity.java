package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.HomeRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.MineDto;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.base.activity.MasterTabActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.RecyclerViewUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ArticleRootEntity;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.module.login.UCManager;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的收藏
 */
public class MineFavoriteActivity extends BaseActivity {

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    //空布局模块
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //适配器的数据列表
//    List<SimpleArticleEntity> recyclerItem;
    //适配器
    HomeRecyclerAdapter recyclerAdapter;

    //下拉刷新的监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //刷新“我的收藏”数据
            refreshFavoriteData();
        }
    };
    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多“我的收藏”数据
            loadMoreFavoriteData();
        }
    };

    //查询Dto
    MineDto dto;

    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;


    //recycler子项的点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            //跳转到文章详情页面
            SimpleArticleEntity simpleArticle = recyclerAdapter.getData().get(position);
            String articleId = simpleArticle.getId();
            String ivMainUrl = simpleArticle.getArticleImg();
            String articleTitle = simpleArticle.getArticleTitle();

            ArticleDetailActivity.startArticleDetailActivity(MineFavoriteActivity.this, articleId, ivMainUrl, articleTitle);
        }
    };

    //recycler子项的孩子的点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener = new BaseQuickAdapter.OnItemChildClickListener() {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            switch (view.getId()) {
                //收藏
                case R.id.iv_favorite:{
                    SimpleArticleEntity simpleArticleEntity = recyclerAdapter.getData().get(position);
                    String articleId = simpleArticleEntity.getId();
                    doCancelFavorite(articleId, position);
                    break;
                }
            }
        }
    };


    @Override
    protected int setContentView() {
        return R.layout.activity_mine_favorite;
    }

    @Override
    protected String settingTitle() {
        return "我的收藏";
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onInitView() {
        //适配器
        recyclerAdapter = new HomeRecyclerAdapter(MineFavoriteActivity.this, R.layout.recycleritem_home, null);
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
        recyclerAdapter.addHeaderView(new View(this));
        //去除默认的动画效果
        ((DefaultItemAnimator) recycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        recycleView.setLayoutManager(new LinearLayoutManager(MineFavoriteActivity.this));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(MineFavoriteActivity.this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(MineFavoriteActivity.this,R.drawable.recycler_divider_custom));
        recycleView.addItemDecoration(divider);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //子项孩子的点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);
        //滑动监听
        try {
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(MineFavoriteActivity.this, fabGotoTop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //限制最大滑动速度
        int maxFlingVelocity = recycleView.getMaxFlingVelocity();
        maxFlingVelocity = getResources().getInteger(R.integer.recycler_view_max_velocity);
//        ToastUtil.showLong(getContext(), "滑动速度：" + maxFlingVelocity);
        RecyclerViewUtil.setMaxFlingVelocity(recycleView, DensityUtil.dip2px(MineFavoriteActivity.this, maxFlingVelocity));

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        //设置样式刷新显示的位置
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_favorite));
        //无数据可点击
        emptyLayout.setClickEnableForNoData(true);
        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载更多“我的收藏”数据
                loadMoreFavoriteData();
            }
        });
        //跳转相关监听
        emptyLayout.setOnGotoRelationListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                Intent intent = new Intent(MineFavoriteActivity.this, MasterTabActivity.class);
                intent.putExtra(MasterTabActivity.VIEWPAGER_POSITION, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);//FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent);
            }
        });

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onInitData() {
        //初始化查询dto
        dto = new MineDto();
        dto.setUserId(UCManager.getInstance().getUserId());
        dto.setPage(pageNum);
        dto.setSize(PAGE_SIZE);

        //加载更多“我的收藏”数据
        loadMoreFavoriteData();
    }

//    @OnClick({R.id.fabGotoTop})
//    public void onViewClick(View view) {
//        switch (view.getId()) {
//            //置顶按钮
//            case R.id.fabGotoTop: {
//                //滚动到顶部
//                recycleView.smoothScrollToPosition(0);
//                break;
//            }
//        }
//    }

    /**
     * 刷新收藏数据
     */
    private void refreshFavoriteData() {
        //下拉刷新
        pageNum = 1;
        //设置页
        dto.setPage(pageNum);
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        //请求收藏数据
        DataRequestService.getInstance().getMyFavorite(dto, new BaseService.ServiceCallback() {
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
                    ArticleRootEntity articleRootEntity = InjectionWrapperUtil.injectMap(dataMap, ArticleRootEntity.class);

                    totalCount = articleRootEntity.getTotal();
                    List<SimpleArticleEntity> dataList = articleRootEntity.getItems();

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
                        if (recyclerAdapter.getData().size() < Dictionary.HIDE_ARTICLE_LOAD_MORE_VIEW_COUNT) {
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
     * 加载更多收藏数据
     */
    private void loadMoreFavoriteData() {
        //设置页
        dto.setPage(pageNum);
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        MineDto dto = new MineDto();
        dto.setUserId(UCManager.getInstance().getUserId());
        dto.setPage(pageNum);
        dto.setSize(PAGE_SIZE);
        DataRequestService.getInstance().getMyFavorite(dto, new BaseService.ServiceCallback() {
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
                    ArticleRootEntity articleRootEntity = InjectionWrapperUtil.injectMap(dataMap, ArticleRootEntity.class);

                    totalCount = articleRootEntity.getTotal();
                    List<SimpleArticleEntity> dataList = articleRootEntity.getItems();

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
                        if (recyclerAdapter.getData().size() < Dictionary.HIDE_ARTICLE_LOAD_MORE_VIEW_COUNT) {
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


    /**
     * 取消收藏
     * @param articleId
     * @param position
     */
    private void doCancelFavorite(String articleId, final int position) {
        DataRequestService.getInstance().postDoFavorite(articleId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
//                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    //刷新收藏视图
//                    Boolean favorite = (Boolean) dataMap.get("is_favorite");
//                    SimpleArticleEntity simpleArticleEntity = recyclerItem.get(position);
//                    simpleArticleEntity.setFavorite(favorite);
                    recyclerAdapter.getData().remove(position);
                    int tempPosition = position + recyclerAdapter.getHeaderLayoutCount();
//                    //把header计算在内
                    recyclerAdapter.notifyItemRemoved(tempPosition);

                    //无数据处理
                    if (recyclerAdapter.getData().size() == 0) {
                        //空布局设置：没有数据
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        //重置数据为空
                        recyclerAdapter.setNewData(null);
                    } else {
                        //把header计算在内
                        recyclerAdapter.notifyItemRemoved(tempPosition);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //操作失败
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        });
    }


}

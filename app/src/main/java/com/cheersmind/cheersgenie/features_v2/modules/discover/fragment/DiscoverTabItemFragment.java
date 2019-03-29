package com.cheersmind.cheersgenie.features_v2.modules.discover.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.HomeRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.entity.ArticleRootEntity;
import com.cheersmind.cheersgenie.features.entity.CategoryEntity;
import com.cheersmind.cheersgenie.features.entity.ChartItem;
import com.cheersmind.cheersgenie.features.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.ChartUtil;
import com.cheersmind.cheersgenie.features.utils.RecyclerViewUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.DiscoverRecyclerAdapter;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 分类
 */
public class DiscoverTabItemFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //类型
    public static final String  CATEGORY = "category";
    private CategoryEntity category;

    private ArticleDto articleDto;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //适配器
    DiscoverRecyclerAdapter recyclerAdapter;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    //下拉刷新的监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            doRefresh();
        }
    };

    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            doLoadMore();
        }
    };

    //recycler子项的点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            //跳转到文章详情页面
            SimpleArticleEntity simpleArticle = (SimpleArticleEntity) recyclerAdapter.getItem(position);
            if (simpleArticle != null) {
                String articleId = simpleArticle.getId();
                String ivMainUrl = simpleArticle.getArticleImg();
                String articleTitle = simpleArticle.getArticleTitle();
                ArticleDetailActivity.startArticleDetailActivity(getContext(), articleId, ivMainUrl, articleTitle);
            }
        }
    };

    //recycler子项的孩子的点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener = new BaseQuickAdapter.OnItemChildClickListener() {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            switch (view.getId()) {
                //收藏
                case R.id.iv_favorite: {
                    SimpleArticleEntity simpleArticleEntity = (SimpleArticleEntity) recyclerAdapter.getItem(position);
                    if (simpleArticleEntity != null) {
                        String articleId = simpleArticleEntity.getId();
                        doFavorite(articleId, position);
                    }
                    break;
                }
            }
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
        return R.layout.fragment_discover_tab_item;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        //设置样式刷新显示的位置
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);

        //适配器
        recyclerAdapter = new DiscoverRecyclerAdapter(getContext(), null);
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
//        recyclerAdapter.addHeaderView(new View(getContext()));
        //去除默认的动画效果
        ((DefaultItemAnimator) recycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_custom));
//        recycleView.addItemDecoration(divider);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //子项孩子的点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);

        //滑动到底部监听，强制停止Fling
        try {
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop) {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    //表示向下滚动,如果为false表示已经到底部了.
                    if (!recyclerView.canScrollVertically(1)) {
                        if (BuildConfig.DEBUG) {
                            System.out.println("停止滚动");
                        }
                        recyclerView.stopScroll();

                    } else {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //限制最大滑动速度
        int maxFlingVelocity = recycleView.getMaxFlingVelocity();
        RecyclerViewUtil.setMaxFlingVelocity(recycleView, DensityUtil.dip2px(getContext(), getResources().getInteger(R.integer.recycler_view_max_velocity)));

        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                doReLoad();
            }
        });

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        //初始的时候就置为加载状态（避免tab切换时，视觉上存在滞后感）
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //初始化数据
        initData();
    }


    private void initData(){
        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                category = (CategoryEntity) bundle.getSerializable(CATEGORY);
                httpTag = bundle.getString(TAG);
                if (TextUtils.isEmpty(httpTag)) {
                    if (getActivity() != null) {
                        ToastUtil.showShort(getActivity().getApplication(), "tag不能为空");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (articleDto == null) {
                articleDto = new ArticleDto();
            }

            articleDto.setPage(pageNum);
            articleDto.setSize(PAGE_SIZE);
            if (category != null) {
                articleDto.setCategoryId(category.getId());
            }
        }

    }


    @Override
    protected void lazyLoad() {
        //加载更多文章数据
        loadMoreArticleData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

        //还原成第一页
        pageNum = 1;
        //后台总记录数
        totalCount = 0;

        //取消当前页面的所有通信
        BaseService.cancelTag(httpTag);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }


    /**
     * 刷新文章数据
     */
    private void refreshArticleData() {
        //下拉刷新
        pageNum = 1;
        //设置页
        articleDto.setPage(pageNum);
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        DataRequestService.getInstance().getArticles(articleDto, new BaseService.ServiceCallback() {
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

                    List<MultiItemEntity> multiItemEntities = articleToMultiItem(dataList);

                    //下拉刷新
                    recyclerAdapter.setNewData(multiItemEntities);
                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        if (recyclerAdapter.getData().size() < Dictionary.HIDE_ARTICLE_LOAD_MORE_VIEW_COUNT_SMALL) {
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
        }, httpTag, getActivity());
    }


    /**
     * SimpleArticleEntity转成List<MultiItemEntity>
      * @param dataList SimpleArticleEntity集合
     * @return List<MultiItemEntity>
     */
    private List<MultiItemEntity> articleToMultiItem(List<SimpleArticleEntity> dataList) {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(dataList)) {
            resList = new ArrayList<>();
            for (SimpleArticleEntity article : dataList) {
                if (article.getContentType() == Dictionary.ARTICLE_TYPE_VIDEO) {
                    article.setItemType(DiscoverRecyclerAdapter.LAYOUT_TYPE_VIDEO);
                } else {
                    article.setItemType(DiscoverRecyclerAdapter.LAYOUT_TYPE_ARTICLE_COMMON);
                }

                resList.add(article);
            }
        }

        return resList;
    }

    /**
     * 加载更多文章数据
     */
    private void loadMoreArticleData() {
        //设置页
        articleDto.setPage(pageNum);
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        DataRequestService.getInstance().getArticles(articleDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //开启下拉刷新功能
                swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突

                if (ArrayListUtil.isEmpty(recyclerAdapter.getData())) {
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

                    List<MultiItemEntity> multiItemEntities = articleToMultiItem(dataList);

                    //当前列表无数据
                    if (recyclerAdapter.getData().size() == 0) {
                        recyclerAdapter.setNewData(multiItemEntities);

                    } else {
                        recyclerAdapter.addData(multiItemEntities);
                    }

                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        if (recyclerAdapter.getData().size() < Dictionary.HIDE_ARTICLE_LOAD_MORE_VIEW_COUNT_SMALL) {
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
                    if (ArrayListUtil.isEmpty(recyclerAdapter.getData())) {
                        //设置空布局：没有数据，可重载
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    } else {
                        //加载失败处理
                        recyclerAdapter.loadMoreFail();
                    }
                }

            }
        }, httpTag, getActivity());
    }


    /**
     * 收藏操作
     *
     * @param articleId 文章ID
     */
    private void doFavorite(String articleId, final int position) {
        DataRequestService.getInstance().postDoFavorite(articleId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    //刷新收藏视图
                    Boolean favorite = (Boolean) dataMap.get("is_favorite");
                    SimpleArticleEntity simpleArticleEntity = (SimpleArticleEntity) recyclerAdapter.getItem(position);
                    if (simpleArticleEntity != null) {
                        simpleArticleEntity.setFavorite(favorite);
                        int tempPosition = position + recyclerAdapter.getHeaderLayoutCount();
                        //局部刷新列表项，把header计算在内
                        recyclerAdapter.notifyItemChanged(tempPosition);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //操作失败
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        }, httpTag, getActivity());
    }


    /**
     * 停止Fling的消息
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onStopFlingNotice(StopFlingEvent event) {
        if (recycleView != null) {
            recycleView.stopScroll();
        }

    }

    /**
     * 刷新
     */
    protected void doRefresh() {
        //刷新文章数据
        refreshArticleData();
    }

    /**
     * 加载更多
     */
    protected void doLoadMore() {
        //加载更多文章数据
        loadMoreArticleData();
    }

    /**
     * 重新加载
     */
    protected void doReLoad() {
        //加载更多文章数据
        loadMoreArticleData();
    }

}

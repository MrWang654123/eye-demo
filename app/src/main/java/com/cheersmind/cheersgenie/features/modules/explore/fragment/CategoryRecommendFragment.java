package com.cheersmind.cheersgenie.features.modules.explore.fragment;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.HomeRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.event.LastHandleExamEvent;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ArticleRootEntity;
import com.cheersmind.cheersgenie.main.entity.CategoryEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;
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

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 推荐
 */
public class CategoryRecommendFragment extends LazyLoadFragment {

    //类型
    public static final String  CATEGORY = "category";
    private CategoryEntity category;

    //通信标记
    public static final String TAG = "tag";
    String tag;

    private ArticleDto articleDto;

    Unbinder unbinder;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //适配器
    HomeRecyclerAdapter recyclerAdapter;

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
            //刷新文章数据
            refreshArticleData();
            //加载最新操作的评测
            loadLastOperateEvaluation();
        }
    };

    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多文章数据
            loadMoreArticleData();
        }
    };

    //recycler子项的点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            //跳转到文章详情页面
            String articleId = recyclerAdapter.getData().get(position).getId();
            ArticleDetailActivity.startArticleDetailActivity(getContext(), articleId);
        }
    };

    //recycler子项的孩子的点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener = new BaseQuickAdapter.OnItemChildClickListener() {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            switch (view.getId()) {
                //收藏
                case R.id.iv_favorite: {
                    SimpleArticleEntity simpleArticleEntity = recyclerAdapter.getData().get(position);
                    String articleId = simpleArticleEntity.getId();
                    doFavorite(articleId, position);
                    break;
                }
            }
        }
    };

    //最新测评模块
    View evaluationBlock;
    //最新评测的标题
    TextView tvLastDimensionTitle;

    //最后一次操作的量表
    DimensionInfoEntity lastDimension;

    //底部滑出显示动画
    private TranslateAnimation mShowAction;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_category_tab_item;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        //设置样式刷新显示的位置
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);

        //适配器
        recyclerAdapter = new HomeRecyclerAdapter(getContext(), R.layout.recycleritem_home, null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        //设置上拉加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(loadMoreListener, recycleView);
        //禁用未满页自动触发上拉加载
        recyclerAdapter.disableLoadMoreIfNotFullPage();
        //设置无文章
//        TextView textView = new TextView(getContext());
//        textView.setWidth(100);
//        textView.setHeight(200);
//        textView.setText("暂无文章");
//        recyclerAdapter.setEmptyView(textView);
        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());
        //预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        recyclerAdapter.setPreLoadNumber(4);
        //添加一个空HeaderView，用于显示顶部分割线
        recyclerAdapter.addHeaderView(new View(getContext()));
        //去除默认的动画效果
        ((DefaultItemAnimator) recycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_custom));
        recycleView.addItemDecoration(divider);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //子项孩子的点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);

        //滑动到底部监听，强制停止Fling
        try {
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(fabGotoTop) {
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
        //滑动到底部监听，强制停止Fling
//        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                //表示向下滚动,如果为false表示已经到底部了.
//                if (!recyclerView.canScrollVertically(1)) {
//                    if (BuildConfig.DEBUG) {
//                        System.out.println("停止滚动");
//                    }
//                    recyclerView.stopScroll();
//                } else {
//                    super.onScrolled(recyclerView, dx, dy);
//
//                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                    if (layoutManager instanceof LinearLayoutManager) {
//                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
////                        int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
//                        int lastVisibleItemPosition = linearManager.findLastVisibleItemPosition();
//                        View viewByPosition = linearManager.findViewByPosition(lastVisibleItemPosition);
//                        int itemAndDividerHeight = viewByPosition.getMeasuredHeight();
//                        //屏幕高度
//                        int screenHeight = QSApplication.getMetrics().heightPixels;
//                        int statusBarHeight = QSApplication.getStatusBarHeight();
//                        int otherViewHeight = DensityUtil.dip2px(getContext(),92 + 55);
//                        int recyclerViewHeight = screenHeight - statusBarHeight - otherViewHeight;
//                        int controlPosition = recyclerViewHeight / itemAndDividerHeight + 1;
//
//                        //向上滑
//                        if (lastVisibleItemPosition == controlPosition && dy>0) {
//                            if (fabGotoTop.getVisibility() != View.VISIBLE) {
////                                fabGotoTop.setVisibility(View.VISIBLE);
//                                fabGotoTop.show();
//                            }
//                        }
//
//                        //向下滑
//                        if (lastVisibleItemPosition == controlPosition && dy<0) {
//                            if (fabGotoTop.getVisibility() == View.VISIBLE) {
////                                fabGotoTop.setVisibility(View.INVISIBLE);
//                                fabGotoTop.hide();
//                            }
//                        }
//                    }
//                    System.out.println("dy：" + dy);
//                }
//            }
//        });

        //设置显示时的动画
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        //最新测评模块
        View view = LayoutInflater.from(recycleView.getContext()).inflate(R.layout.recycler_header_home_evaluation, recycleView, false);
        evaluationBlock = getLayoutInflater().inflate(R.layout.recycler_header_home_evaluation, null);
        //测评标题
        tvLastDimensionTitle = evaluationBlock.findViewById(R.id.tv_last_dimension_title);
        //进入最新测评按钮
        Button btnGotoLastDimension = evaluationBlock.findViewById(R.id.btn_goto_last_dimension);
        btnGotoLastDimension.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                DimensionDetailActivity.startDimensionDetailActivity(getContext(), lastDimension, null);
            }
        });

        //添加为recyclerView的header
        recyclerAdapter.addHeaderView(evaluationBlock);
        //初始隐藏最新评测模块
        evaluationBlock.setVisibility(View.GONE);

        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载更多文章数据
                loadMoreArticleData();
                //加载最新操作的评测
                loadLastOperateEvaluation();
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
                tag = bundle.getString(TAG);
                if (TextUtils.isEmpty(tag)) {
                    ToastUtil.showShort(getContext(), "tag不能为空");
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
        //加载最新操作的评测
        loadLastOperateEvaluation();
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
        BaseService.cancelTag(tag);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.fabGotoTop})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //置顶按钮
            case R.id.fabGotoTop: {
                //滚动到顶部
                recycleView.smoothScrollToPosition(0);
                break;
            }
        }
    }

    /**
     * 刷新文章数据
     */
    private void refreshArticleData() {
        //下拉刷新
        pageNum = 1;
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

                    //下拉刷新
                    recyclerAdapter.setNewData(dataList);
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
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    //清空列表数据
                    recyclerAdapter.setNewData(null);
                }

            }
        }, tag);
    }

    /**
     * 加载更多文章数据
     */
    private void loadMoreArticleData() {
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
                        recyclerAdapter.loadMoreEnd();
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
        }, tag);
    }


    /**
     * 收藏操作
     *
     * @param articleId
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
                    SimpleArticleEntity simpleArticleEntity = recyclerAdapter.getData().get(position);
                    simpleArticleEntity.setFavorite(favorite);
                    int tempPosition = position + recyclerAdapter.getHeaderLayoutCount();
                    //局部刷新列表项，把header计算在内
                    recyclerAdapter.notifyItemChanged(tempPosition);

                } catch (Exception e) {
                    e.printStackTrace();
                    //操作失败
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        }, tag);
    }


    /**
     * 停止Fling的消息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onStopFlingNotice(StopFlingEvent event) {
        if (recycleView != null) {
            recycleView.stopScroll();
//            recycleView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
        }

    }


    /**
     * 最新操作测评消息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onLastExamNotice(LastHandleExamEvent event) {
        if (event == null) {
            return;
        }

        //更新最新测评
        if (event.getHandleType() == LastHandleExamEvent.HANDLE_TYPE_UPDATE) {
            //加载最新操作的评测
            loadLastOperateEvaluation();

        } else if (event.getHandleType() == LastHandleExamEvent.HANDLE_TYPE_COMPLETE) {//刚完成一个新的测评，此时无最新操作测评
            //隐藏视图，清理数据
            lastDimension = null;
            evaluationBlock.setVisibility(View.GONE);
        }

    }


    /**
     * 获取最新操作的评测
     */
    public void loadLastOperateEvaluation() {
        String defaultChildId = ChildInfoDao.getDefaultChildId();
        DataRequestService.getInstance().getLatestDimensionV2(defaultChildId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                lastDimension = null;
                evaluationBlock.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    lastDimension = InjectionWrapperUtil.injectMap(dataMap, DimensionInfoEntity.class);
                    //有量表对象，且量表处于未完成状态
                    if (lastDimension != null
                            && !TextUtils.isEmpty(lastDimension.getDimensionId())
                            && lastDimension.getChildDimension() != null
                            && lastDimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                        //刷新最新操作测评的视图
                        tvLastDimensionTitle.setText(lastDimension.getDimensionName());

                    } else {
                        //没数据或者数据异常
                        throw new Exception();
                    }

                    evaluationBlock.setVisibility(View.VISIBLE);
                    evaluationBlock.startAnimation(mShowAction);
                    //空布局：隐藏
//                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                } catch (Exception e) {
                    e.printStackTrace();
                    lastDimension = null;
                    evaluationBlock.setVisibility(View.GONE);
                }

            }
        }, tag);
    }


}
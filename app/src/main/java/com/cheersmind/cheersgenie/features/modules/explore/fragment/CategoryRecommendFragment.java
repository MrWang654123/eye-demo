package com.cheersmind.cheersgenie.features.modules.explore.fragment;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
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
import com.cheersmind.cheersgenie.features.utils.RecyclerViewUtil;
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
import com.facebook.drawee.view.SimpleDraweeView;

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
    String httpTag;

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
            SimpleArticleEntity simpleArticle = recyclerAdapter.getData().get(position);
            String articleId = simpleArticle.getId();
            String ivMainUrl = simpleArticle.getArticleImg();
            String articleTitle = simpleArticle.getArticleTitle();

            ArticleDetailActivity.startArticleDetailActivity(getContext(), articleId, ivMainUrl, articleTitle);
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

    @SuppressLint("RestrictedApi")
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
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop) {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

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

        //限制最大滑动速度
        int maxFlingVelocity = recycleView.getMaxFlingVelocity();
        maxFlingVelocity = getResources().getInteger(R.integer.recycler_view_max_velocity);
//        ToastUtil.showLong(getContext(), "滑动速度：" + maxFlingVelocity);
        RecyclerViewUtil.setMaxFlingVelocity(recycleView,DensityUtil.dip2px(getContext(), maxFlingVelocity));

        //设置显示时的动画
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        //最新测评模块
//        View view = LayoutInflater.from(recycleView.getContext()).inflate(R.layout.recycler_header_home_evaluation, recycleView, false);
        evaluationBlock = getLayoutInflater().inflate(R.layout.recycler_header_home_evaluation, null);
        //测评标题
        tvLastDimensionTitle = evaluationBlock.findViewById(R.id.tv_last_dimension_title);
        //进入最新测评按钮
        Button btnGotoLastDimension = evaluationBlock.findViewById(R.id.btn_goto_last_dimension);
        btnGotoLastDimension.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                DimensionDetailActivity.startDimensionDetailActivity(getContext(),
                        lastDimension, null,
                        Dictionary.EXAM_STATUS_DOING,
                        Dictionary.FROM_ACTIVITY_TO_QUESTION_MAIN);
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
        BaseService.cancelTag(httpTag);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
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

                    //下拉刷新
                    recyclerAdapter.setNewData(dataList);
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

//                    Map dataMap = JsonUtil.fromJson(testArticleStr, Map.class);
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
                    if (recyclerAdapter.getData().size() == 0) {
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
        }, httpTag, getActivity());
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
                        throw new QSCustomException("无最新操作测评");
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
        }, httpTag, getActivity());
    }


//    private String testArticleStr = "{\n" +
//            "\t\"total\": 33,\n" +
//            "\t\"items\": [{\n" +
//            "\t\t\"id\": \"c6660405-987a-45aa-9819-fd6344242f6b\",\n" +
//            "\t\t\"article_title\": \"你抑郁了吗？家中如果有青少年感觉忧虑及抑郁要怎么办？\",\n" +
//            "\t\t\"article_img\": \"http:\\/\\/p3.pstatp.com\\/large\\/pgc-image\\/15220677529242047f53aef\",\n" +
//            "\t\t\"is_reference_test\": 1,\n" +
//            "\t\t\"summary\": \"你抑郁了吗？家中如果有青少年感觉忧虑及抑郁要怎么办？\",\n" +
//            "\t\t\"content_type\": 2,\n" +
//            "\t\t\"source_type\": 2,\n" +
//            "\t\t\"page_view\": 485,\n" +
//            "\t\t\"page_favorite\": 2,\n" +
//            "\t\t\"test_count\": 56,\n" +
//            "\t\t\"source_name\": \"少年计划YouthPlan\",\n" +
//            "\t\t\"page_like\": 1,\n" +
//            "\t\t\"is_like\": false,\n" +
//            "\t\t\"is_favorite\": false,\n" +
//            "\t  \"comment_count\": 213,\n" +
//            "\t\t\"user_data\": {\n" +
//            "\t\t\t\"user_id\": null,\n" +
//            "\t\t\t\"user_name\": null,\n" +
//            "\t\t\t\"nick_name\": null,\n" +
//            "\t\t\t\"avatar\": null\n" +
//            "\t\t},\n" +
//            "\t\t\"category\": {\n" +
//            "\t\t\t\"id\": \"fe31d362-9e5c-484d-8965-517e73fa29f6\",\n" +
//            "\t\t\t\"name\": \"新高考3+3\",\n" +
//            "\t\t\t\"background_img\": \"\",\n" +
//            "\t\t\t\"background_color\": null,\n" +
//            "\t\t\t\"icon\": null\n" +
//            "\t\t}\n" +
//            "\t}, {\n" +
//            "\t\t\"id\": \"a41f6717-076f-4e93-a2b2-b4aa56909d84\",\n" +
//            "\t\t\"article_title\": \"通晓工作及职业技能的七条途径\",\n" +
//            "\t\t\"article_img\": \"http:\\/\\/p99.pstatp.com\\/large\\/pgc-image\\/1521785903939fea94c942d\",\n" +
//            "\t\t\"is_reference_test\": 0,\n" +
//            "\t\t\"summary\": \"通晓工作及职业技能的七条途径\",\n" +
//            "\t\t\"content_type\": 1,\n" +
//            "\t\t\"source_type\": 2,\n" +
//            "\t\t\"page_view\": 239,\n" +
//            "\t\t\"page_favorite\": 4,\n" +
//            "\t\t\"test_count\": null,\n" +
//            "\t\t\"source_name\": \"少年计划YouthPlan\",\n" +
//            "\t\t\"page_like\": 2,\n" +
//            "\t\t\"is_like\": false,\n" +
//            "\t\t\"is_favorite\": false,\n" +
//            "\t  \"comment_count\": 213,\n" +
//            "\t\t\"user_data\": {\n" +
//            "\t\t\t\"user_id\": null,\n" +
//            "\t\t\t\"user_name\": null,\n" +
//            "\t\t\t\"nick_name\": null,\n" +
//            "\t\t\t\"avatar\": null\n" +
//            "\t\t},\n" +
//            "\t\t\"category\": {\n" +
//            "\t\t\t\"id\": \"375a9275-f2f2-4787-b898-0311d238e981\",\n" +
//            "\t\t\t\"name\": \"学习习惯3\",\n" +
//            "\t\t\t\"background_img\": \"\",\n" +
//            "\t\t\t\"background_color\": null,\n" +
//            "\t\t\t\"icon\": null\n" +
//            "\t\t}\n" +
//            "\t}, {\n" +
//            "\t\t\"id\": \"f4a34466-5d03-4ab9-9a40-28cc602d6976\",\n" +
//            "\t\t\"article_title\": \"如何辨别你的父母是否对你控制欲过强\",\n" +
//            "\t\t\"article_img\": \"http:\\/\\/p3.pstatp.com\\/large\\/pgc-image\\/1521785253364835d85d924\",\n" +
//            "\t\t\"is_reference_test\": 0,\n" +
//            "\t\t\"summary\": \"如何辨别你的父母是否对你控制欲过强\",\n" +
//            "\t\t\"content_type\": 0,\n" +
//            "\t\t\"source_type\": 2,\n" +
//            "\t\t\"page_view\": 265,\n" +
//            "\t\t\"page_favorite\": 5,\n" +
//            "\t\t\"test_count\": null,\n" +
//            "\t\t\"source_name\": \"少年计划YouthPlan\",\n" +
//            "\t\t\"page_like\": 0,\n" +
//            "\t\t\"is_like\": false,\n" +
//            "\t\t\"is_favorite\": true,\n" +
//            "\t  \"comment_count\": 43,\n" +
//            "\t\t\"user_data\": {\n" +
//            "\t\t\t\"user_id\": null,\n" +
//            "\t\t\t\"user_name\": null,\n" +
//            "\t\t\t\"nick_name\": null,\n" +
//            "\t\t\t\"avatar\": null\n" +
//            "\t\t},\n" +
//            "\t\t\"category\": {\n" +
//            "\t\t\t\"id\": \"375a9275-f2f2-4787-b898-0311d238e981\",\n" +
//            "\t\t\t\"name\": \"学习习惯3\",\n" +
//            "\t\t\t\"background_img\": \"\",\n" +
//            "\t\t\t\"background_color\": null,\n" +
//            "\t\t\t\"icon\": null\n" +
//            "\t\t}\n" +
//            "\t}, {\n" +
//            "\t\t\"id\": \"e22fe967-5bf8-4104-9ab6-24070e83ccc2\",\n" +
//            "\t\t\"article_title\": \"时间对每个人都是公平的，你没有掌握的时间管理技巧\",\n" +
//            "\t\t\"article_img\": \"http:\\/\\/p1.pstatp.com\\/large\\/pgc-image\\/15300169928267b9a48d9b6\",\n" +
//            "\t\t\"is_reference_test\": 0,\n" +
//            "\t\t\"summary\": \"时间对每个人都是公平的，你没有掌握的时间管理技巧\",\n" +
//            "\t\t\"content_type\": 1,\n" +
//            "\t\t\"source_type\": 2,\n" +
//            "\t\t\"page_view\": 230,\n" +
//            "\t\t\"page_favorite\": 1,\n" +
//            "\t\t\"test_count\": null,\n" +
//            "\t\t\"source_name\": \"少年计划YouthPlan\",\n" +
//            "\t\t\"page_like\": 1,\n" +
//            "\t\t\"is_like\": false,\n" +
//            "\t\t\"is_favorite\": true,\n" +
//            "\t  \"comment_count\": 8,\n" +
//            "\t\t\"user_data\": {\n" +
//            "\t\t\t\"user_id\": null,\n" +
//            "\t\t\t\"user_name\": null,\n" +
//            "\t\t\t\"nick_name\": null,\n" +
//            "\t\t\t\"avatar\": null\n" +
//            "\t\t},\n" +
//            "\t\t\"category\": null\n" +
//            "\t}, {\n" +
//            "\t\t\"id\": \"bdafc1cb-59aa-44ca-b8c9-bb816090de0c\",\n" +
//            "\t\t\"article_title\": \"7种父母的感情影响你处理感情的方式\",\n" +
//            "\t\t\"article_img\": \"http:\\/\\/p1.pstatp.com\\/large\\/pgc-image\\/153020139251126968133ba\",\n" +
//            "\t\t\"is_reference_test\": 0,\n" +
//            "\t\t\"summary\": \"7种父母的感情影响你处理感情的方式\",\n" +
//            "\t\t\"content_type\": 1,\n" +
//            "\t\t\"source_type\": 2,\n" +
//            "\t\t\"page_view\": 199,\n" +
//            "\t\t\"page_favorite\": 4,\n" +
//            "\t\t\"test_count\": null,\n" +
//            "\t\t\"source_name\": \"少年计划YouthPlan\",\n" +
//            "\t\t\"page_like\": 1,\n" +
//            "\t\t\"is_like\": false,\n" +
//            "\t\t\"is_favorite\": false,\n" +
//            "\t\t\"user_data\": {\n" +
//            "\t\t\t\"user_id\": null,\n" +
//            "\t\t\t\"user_name\": null,\n" +
//            "\t\t\t\"nick_name\": null,\n" +
//            "\t\t\t\"avatar\": null\n" +
//            "\t\t},\n" +
//            "\t\t\"category\": null\n" +
//            "\t}, {\n" +
//            "\t\t\"id\": \"44942338-e64b-4c3d-8fa2-016075274e02\",\n" +
//            "\t\t\"article_title\": \"雅思小作文 - 如何有效组织写作内容\",\n" +
//            "\t\t\"article_img\": \"http:\\/\\/p3.pstatp.com\\/origin\\/79e300037ec5c04bebe4\",\n" +
//            "\t\t\"is_reference_test\": 0,\n" +
//            "\t\t\"summary\": \"雅思小作文 - 如何有效组织写作内容\",\n" +
//            "\t\t\"content_type\": 2,\n" +
//            "\t\t\"source_type\": 2,\n" +
//            "\t\t\"page_view\": 307,\n" +
//            "\t\t\"page_favorite\": 3,\n" +
//            "\t\t\"test_count\": null,\n" +
//            "\t\t\"source_name\": \"少年计划YouthPlan\",\n" +
//            "\t\t\"page_like\": 1,\n" +
//            "\t\t\"is_like\": false,\n" +
//            "\t\t\"is_favorite\": true,\n" +
//            "\t\t\"user_data\": {\n" +
//            "\t\t\t\"user_id\": null,\n" +
//            "\t\t\t\"user_name\": null,\n" +
//            "\t\t\t\"nick_name\": null,\n" +
//            "\t\t\t\"avatar\": null\n" +
//            "\t\t},\n" +
//            "\t\t\"category\": {\n" +
//            "\t\t\t\"id\": null,\n" +
//            "\t\t\t\"name\": null,\n" +
//            "\t\t\t\"background_img\": null,\n" +
//            "\t\t\t\"background_color\": null,\n" +
//            "\t\t\t\"icon\": null\n" +
//            "\t\t}\n" +
//            "\t}, {\n" +
//            "\t\t\"id\": \"568637cb-14cd-4108-8703-6067093982e5\",\n" +
//            "\t\t\"article_title\": \"雅思写作小作文9分技巧及词汇\",\n" +
//            "\t\t\"article_img\": \"http:\\/\\/p3.pstatp.com\\/origin\\/7a06000cedf3693300f8\",\n" +
//            "\t\t\"is_reference_test\": 0,\n" +
//            "\t\t\"summary\": \"雅思写作小作文9分技巧及词汇\",\n" +
//            "\t\t\"content_type\": 2,\n" +
//            "\t\t\"source_type\": 2,\n" +
//            "\t\t\"page_view\": 340,\n" +
//            "\t\t\"page_favorite\": 3,\n" +
//            "\t\t\"test_count\": null,\n" +
//            "\t\t\"source_name\": \"少年计划YouthPlan\",\n" +
//            "\t\t\"page_like\": 3,\n" +
//            "\t\t\"is_like\": false,\n" +
//            "\t\t\"is_favorite\": false,\n" +
//            "\t\t\"user_data\": {\n" +
//            "\t\t\t\"user_id\": null,\n" +
//            "\t\t\t\"user_name\": null,\n" +
//            "\t\t\t\"nick_name\": null,\n" +
//            "\t\t\t\"avatar\": null\n" +
//            "\t\t},\n" +
//            "\t\t\"category\": null\n" +
//            "\t}, {\n" +
//            "\t\t\"id\": \"e22fe967-5bf8-4104-9ab6-24070e83ccc3\",\n" +
//            "\t\t\"article_title\": \"英文听读-最聪明的5%的人才能解开的7大谜团，敢来一试？\",\n" +
//            "\t\t\"article_img\": \"http:\\/\\/p3.pstatp.com\\/origin\\/a8560004952f908dd678\",\n" +
//            "\t\t\"is_reference_test\": 0,\n" +
//            "\t\t\"summary\": \"英文听读-最聪明的5%的人才能解开的7大谜团，敢来一试？\",\n" +
//            "\t\t\"content_type\": 2,\n" +
//            "\t\t\"source_type\": 2,\n" +
//            "\t\t\"page_view\": 209,\n" +
//            "\t\t\"page_favorite\": 2,\n" +
//            "\t\t\"test_count\": null,\n" +
//            "\t\t\"source_name\": \"少年计划YouthPlan\",\n" +
//            "\t\t\"page_like\": 0,\n" +
//            "\t\t\"is_like\": false,\n" +
//            "\t\t\"is_favorite\": true,\n" +
//            "\t\t\"user_data\": {\n" +
//            "\t\t\t\"user_id\": null,\n" +
//            "\t\t\t\"user_name\": null,\n" +
//            "\t\t\t\"nick_name\": null,\n" +
//            "\t\t\t\"avatar\": null\n" +
//            "\t\t},\n" +
//            "\t\t\"category\": null\n" +
//            "\t}, {\n" +
//            "\t\t\"id\": \"3200c33b-21d3-445b-b22c-b4b72bd30f03\",\n" +
//            "\t\t\"article_title\": \"视觉魔法分形艺术\",\n" +
//            "\t\t\"article_img\": \"http:\\/\\/p3.pstatp.com\\/large\\/pgc-image\\/1521613623363d573ee8471\",\n" +
//            "\t\t\"is_reference_test\": 0,\n" +
//            "\t\t\"summary\": \"视觉魔法分形艺术\",\n" +
//            "\t\t\"content_type\": 1,\n" +
//            "\t\t\"source_type\": 2,\n" +
//            "\t\t\"page_view\": 220,\n" +
//            "\t\t\"page_favorite\": 5,\n" +
//            "\t\t\"test_count\": null,\n" +
//            "\t\t\"source_name\": \"少年计划YouthPlan\",\n" +
//            "\t\t\"page_like\": 4,\n" +
//            "\t\t\"is_like\": true,\n" +
//            "\t\t\"is_favorite\": true,\n" +
//            "\t\t\"user_data\": {\n" +
//            "\t\t\t\"user_id\": null,\n" +
//            "\t\t\t\"user_name\": null,\n" +
//            "\t\t\t\"nick_name\": null,\n" +
//            "\t\t\t\"avatar\": null\n" +
//            "\t\t},\n" +
//            "\t\t\"category\": {\n" +
//            "\t\t\t\"id\": null,\n" +
//            "\t\t\t\"name\": null,\n" +
//            "\t\t\t\"background_img\": null,\n" +
//            "\t\t\t\"background_color\": null,\n" +
//            "\t\t\t\"icon\": null\n" +
//            "\t\t}\n" +
//            "\t}, {\n" +
//            "\t\t\"id\": \"efddac3c-1434-411a-bcb4-80a96bf4b6f9\",\n" +
//            "\t\t\"article_title\": \"雅思写作 - 分析表图（1）\",\n" +
//            "\t\t\"article_img\": null,\n" +
//            "\t\t\"is_reference_test\": 0,\n" +
//            "\t\t\"summary\": \"雅思写作 - 分析表图（1）\",\n" +
//            "\t\t\"content_type\": 1,\n" +
//            "\t\t\"source_type\": 2,\n" +
//            "\t\t\"page_view\": 93,\n" +
//            "\t\t\"page_favorite\": 3,\n" +
//            "\t\t\"test_count\": null,\n" +
//            "\t\t\"source_name\": \"少年计划YouthPlan\",\n" +
//            "\t\t\"page_like\": 0,\n" +
//            "\t\t\"is_like\": false,\n" +
//            "\t\t\"is_favorite\": false,\n" +
//            "\t\t\"user_data\": {\n" +
//            "\t\t\t\"user_id\": null,\n" +
//            "\t\t\t\"user_name\": null,\n" +
//            "\t\t\t\"nick_name\": null,\n" +
//            "\t\t\t\"avatar\": null\n" +
//            "\t\t},\n" +
//            "\t\t\"category\": null\n" +
//            "\t}]\n" +
//            "}";

}

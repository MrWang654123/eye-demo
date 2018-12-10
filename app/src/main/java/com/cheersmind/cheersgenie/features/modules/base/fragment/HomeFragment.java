package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.HomeRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.dto.BaseDto;
import com.cheersmind.cheersgenie.features.event.LastHandleExamEvent;
import com.cheersmind.cheersgenie.features.holder.BannerHomeHolder;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.modules.article.activity.SearchArticleActivity;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.CategoryDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ArticleRootEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 首页
 */
public class HomeFragment extends LazyLoadFragment {

    @BindView(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    //banner
//    @BindView(R.id.banner)
//    Banner banner;
    //最新测评模块
    @BindView(R.id.evaluation_block)
    View evaluationBlock;
    //最新评测的标题
    @BindView(R.id.tv_last_dimension_title)
    TextView tvLastDimensionTitle;


    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    Unbinder unbinder;


    private String[] images = {"http://img2.imgtn.bdimg.com/it/u=3093785514,1341050958&fm=21&gp=0.jpg",
            "http://img2.3lian.com/2014/f2/37/d/40.jpg",
            "http://d.3987.com/sqmy_131219/001.jpg",
            "http://img2.3lian.com/2014/f2/37/d/39.jpg",
            "http://www.8kmm.com/UploadFiles/2012/8/201208140920132659.jpg",
            "http://f.hiphotos.baidu.com/image/h%3D200/sign=1478eb74d5a20cf45990f9df460b4b0c/d058ccbf6c81800a5422e5fdb43533fa838b4779.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/09fa513d269759ee50f1971ab6fb43166c22dfba.jpg"
    };
    private String[] images2 = {"http://d.3987.com/sqmy_131219/001.jpg",
            "http://img2.3lian.com/2014/f2/37/d/39.jpg"
    };
    //banner子项点击监听
    OnItemClickListener bannerItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            handlerBannerItemClick(position);
        }
    };
    //banner的ViewHolderCreator
    CBViewHolderCreator viewHolderCreator = new CBViewHolderCreator() {
        @Override
        public Holder createHolder(View itemView) {
            return new BannerHomeHolder(HomeFragment.this, itemView);
        }

        @Override
        public int getLayoutId() {
            return R.layout.banneritem_home;
        }
    };

    //适配器的数据列表
//    List<SimpleArticleEntity> recyclerItem;
    //适配器
    HomeRecyclerAdapter recyclerAdapter;

    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //下拉刷新的监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //还原通信错误数量
            errorQuantity = 0;

            //加载banner
            loadBannerData();
            //加载最新操作的评测
            loadLastOperateEvaluation();
            //刷新文章数据
            refreshArticleData();
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

    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    //底部滑出显示动画
    private TranslateAnimation mShowAction;

    //recycler子项的点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//            ToastUtil.showShort(getContext(), "点击第" + (position + 1) + "项");
            //跳转到文章详情页面
            SimpleArticleEntity simpleArticle = recyclerAdapter.getData().get(position);
            String articleId = simpleArticle.getId();
            String ivMainUrl = simpleArticle.getArticleImg();
            String articleTitle = simpleArticle.getArticleTitle();

            ArticleDetailActivity.startArticleDetailActivity(getContext(), articleId, ivMainUrl, articleTitle);
//            VideoActivity.startVideoActivity(getContext(), articleId);
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

    //banner翻页间隔时间
    private static final int BANNER_AUTO_NEXT_PAGE_TIME = 5000;

    //最后一次操作的量表
    DimensionInfoEntity lastDimension;

    //通信错误数量，目前本页面总共3个通信
    int errorQuantity = 0;
    private final static int MAX_ERROR_QUANTITY = 3;
    //消息：错误数量
    private static final int MSG_ERROR_QUANTITY = 1;


    //banner文章集合
    List<SimpleArticleEntity> bannerArticleList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onInitView(View contentView) {
        unbinder = ButterKnife.bind(this, contentView);

        //初始隐藏banner和最新评测模块
//        banner.setVisibility(View.GONE);
        convenientBanner.setVisibility(View.GONE);
        evaluationBlock.setVisibility(View.GONE);

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

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        //设置样式刷新显示的位置
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);


        //设置显示时的动画
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        //监听 AppBarLayout Offset 变化，动态设置 SwipeRefreshLayout 是否可用
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //还原通信错误数量
                errorQuantity = 0;
                //设置空布局，当前列表还没有数据的情况，提示：通信等待提示中
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

                //加载banner
                loadBannerData();
                //加载最新操作的评测
                loadLastOperateEvaluation();
                //加载更多文章数据
                loadMoreArticleData();
            }
        });

        //动态计算banner的高度
        final DisplayMetrics metrics = QSApplication.getMetrics();
        int itemWidth = metrics.widthPixels;
        final int itemHeight = (int) (itemWidth * (9f/16));
        ViewTreeObserver vto = convenientBanner.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                convenientBanner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                int width = convenientBanner.getWidth();
//                final int resHeight = (int)(width * (310.0f / 398));

                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) convenientBanner.getLayoutParams();
                params.width = metrics.widthPixels;
                params.height = itemHeight;
                convenientBanner.setLayoutParams(params);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        if (!ArrayListUtil.isEmpty(bannerArticleList)) {
            //开始轮播
//            banner.startAutoPlay();
            convenientBanner.startTurning();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        if (!ArrayListUtil.isEmpty(bannerArticleList)) {
            //结束轮播
//            banner.stopAutoPlay();
            convenientBanner.stopTurning();
        }
        //取消toast
        ToastUtil.cancelToast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void lazyLoad() {
        //设置空布局，当前列表还没有数据的情况，提示：通信等待提示中
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //加载banner
        loadBannerData();
        //加载最新操作的评测
        loadLastOperateEvaluation();
        //加载更多文章数据
        loadMoreArticleData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 加载banner数据（目前指的是热门文章）
     */
    public void loadBannerData() {
        BaseDto dto = new BaseDto(1, 10);
        DataRequestService.getInstance().getHotArticles(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
//                banner.setVisibility(View.GONE);
                convenientBanner.setVisibility(View.GONE);
                //发送通信错误消息
                getHandler().sendEmptyMessage(MSG_ERROR_QUANTITY);
            }

            @Override
            public void onResponse(Object obj) {
                //banner
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ArticleRootEntity articleRootEntity = InjectionWrapperUtil.injectMap(dataMap, ArticleRootEntity.class);
                    //文章集合
                    bannerArticleList = articleRootEntity.getItems();
                    //非空
                    if (ArrayListUtil.isNotEmpty(bannerArticleList)) {
//                        bannerlist = Arrays.asList(images);
                        List<String> images = new ArrayList<>();
                        for (SimpleArticleEntity simpleArticle : bannerArticleList) {
                            images.add(simpleArticle.getArticleImg());
                        }

                        //设置图片加载器
                        /*banner.setImageLoader(new GlideImageLoader());
                        //设置图片集合
                        banner.setImages(images);
                        //banner设置方法全部调用完毕时最后调用
                        banner.start();
                        banner.setVisibility(View.VISIBLE);
                        banner.startAnimation(mShowAction);
                        */

//                        ConvenientBanner convenientBanner = new ConvenientBanner();
                        convenientBanner.setPages(viewHolderCreator, bannerArticleList) //mList是图片地址的集合
                                .setPointViewVisible(true)    //设置指示器是否可见
                                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                                //设置指示器位置（左、中、右）
                                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                                .setOnItemClickListener(bannerItemClickListener)//点击监听
                                .startTurning(BANNER_AUTO_NEXT_PAGE_TIME);     //设置自动切换（同时设置了切换时间间隔）
//                .setManualPageable(true)  //设置手动影响（设置了该项无法手动切换）
//        ;
                        convenientBanner.setVisibility(View.VISIBLE);
                        convenientBanner.startAnimation(mShowAction);
                        //空布局：隐藏
                        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    } else {
                        //没数据或者数据异常
                        throw new Exception();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    banner.setVisibility(View.GONE);
                    convenientBanner.setVisibility(View.GONE);
                }

            }
        }, httpTag, getActivity());
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
                //发送通信错误消息
                getHandler().sendEmptyMessage(MSG_ERROR_QUANTITY);
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
                        throw new QSCustomException("没有最新最测评");
                    }

                    evaluationBlock.setVisibility(View.VISIBLE);
                    evaluationBlock.startAnimation(mShowAction);
                    //空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                } catch (QSCustomException e) {
                    lastDimension = null;
                    evaluationBlock.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    lastDimension = null;
                    evaluationBlock.setVisibility(View.GONE);
                }

            }
        }, httpTag, getActivity());
    }

    /**
     * 刷新文章列表
     */
    private void refreshArticleData() {
        //下拉刷新
        pageNum = 1;
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        ArticleDto dto = new ArticleDto(pageNum, PAGE_SIZE);
        DataRequestService.getInstance().getArticles(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //发送通信错误消息
                getHandler().sendEmptyMessage(MSG_ERROR_QUANTITY);

                //开启上拉加载功能
                recyclerAdapter.setEnableLoadMore(true);
                //结束下拉刷新动画
                swipeRefreshLayout.setRefreshing(false);
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

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ArticleRootEntity articleRootEntity = InjectionWrapperUtil.injectMap(dataMap, ArticleRootEntity.class);

                    totalCount = articleRootEntity.getTotal();
                    List<SimpleArticleEntity> dataList = articleRootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        //显示暂无文章
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
                    //设置空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                } catch (Exception e) {
                    e.printStackTrace();
                    //发送通信错误消息
                    getHandler().sendEmptyMessage(MSG_ERROR_QUANTITY);
                    //清空列表数据
                    recyclerAdapter.setNewData(null);
                    //加载失败处理
                    recyclerAdapter.loadMoreFail();
                }

            }
        }, httpTag, getActivity());
    }

    /**
     * 加载更多文章
     */
    private void loadMoreArticleData() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        ArticleDto dto = new ArticleDto(pageNum, PAGE_SIZE);
        DataRequestService.getInstance().getArticles(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //发送通信错误消息
                getHandler().sendEmptyMessage(MSG_ERROR_QUANTITY);

                //开启下拉刷新功能
                swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突

                //加载失败处理
                recyclerAdapter.loadMoreFail();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //开启下拉刷新功能
                    swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ArticleRootEntity articleRootEntity = InjectionWrapperUtil.injectMap(dataMap, ArticleRootEntity.class);

                    totalCount = articleRootEntity.getTotal();
                    List<SimpleArticleEntity> dataList = articleRootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        //显示暂无文章
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
                    //空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                } catch (Exception e) {
                    e.printStackTrace();
                    //发送通信错误消息
                    getHandler().sendEmptyMessage(MSG_ERROR_QUANTITY);
                    //加载失败处理
                    recyclerAdapter.loadMoreFail();
                }

            }
        }, httpTag, getActivity());
    }

    //获取首页数据列表（目前是热门文章）
    /*public void loadHomeDataList() {
        //设置空布局，当前列表还没有数据的情况，提示：通信等待提示中
//        if (ArrayListUtil.isEmpty(recyclerItem)) {
//            xemptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
//        }
        if (swipeRefreshLayout.isRefreshing()) {
            //下拉刷新
            pageNum = 1;
            recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        }

        ArticleDto dto = new ArticleDto(pageNum, PAGE_SIZE);
        DataRequestService.getInstance().getArticles(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //发送通信错误消息
                mHandler.sendEmptyMessage(MSG_ERROR_QUANTITY);

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
//                if (ArrayListUtil.isEmpty(recyclerItem)) {
//                    xemptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
//                }
            }

            @Override
            public void onResponse(Object obj) {
                //设置空布局
//                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

                if (obj != null) {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ArticleRootEntity articleRootEntity = InjectionWrapperUtil.injectMap(dataMap, ArticleRootEntity.class);

                    totalCount = articleRootEntity.getTotal();
                    List<SimpleArticleEntity> dataList = articleRootEntity.getItems();

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
                    //空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                } else {
                    //设置空布局：当前列表还没有数据的情况，提示：没有数据
//                    if (ArrayListUtil.isEmpty(recyclerItem)) {
//                        xemptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
//                    }
                }

                //下拉刷新，还原动画
                if (swipeRefreshLayout.isRefreshing()) {
                    //开启上拉加载功能
                    recyclerAdapter.setEnableLoadMore(true);
                    //结束下拉刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }*/

    /**
     * 处理banner点击事件
     *
     * @param position
     */
    private void handlerBannerItemClick(int position) {
        //跳转到文章详情页面
        SimpleArticleEntity simpleArticle = bannerArticleList.get(position);
        String articleId = simpleArticle.getId();
        String ivMainUrl = simpleArticle.getArticleImg();
        String articleTitle = simpleArticle.getArticleTitle();

        ArticleDetailActivity.startArticleDetailActivity(getContext(), articleId, ivMainUrl, articleTitle);
//        ToastUtil.showShort(getContext(), "点击了第" + (position + 1) + "页");
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


    @OnClick({R.id.iv_category, R.id.btn_goto_last_dimension, R.id.iv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //分类
            case R.id.iv_category: {
                new CategoryDialog(getActivity(), null).show();
                break;
            }
            //进入最新测评
            case R.id.btn_goto_last_dimension: {
                DimensionDetailActivity.startDimensionDetailActivity(getContext(), lastDimension, null);
                break;
            }
            //搜索
            case R.id.iv_search: {
                SearchArticleActivity.startSearchArticleActivity(getContext(), null);
                break;
            }
        }
    }

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            //通信错误消息
            case MSG_ERROR_QUANTITY: {
                errorQuantity++;
                //如果所有通信都错误，则空布局提示网络错误
                if (errorQuantity == MAX_ERROR_QUANTITY) {
                    //空布局：网络错误
                    emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                }
                break;
            }
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


}

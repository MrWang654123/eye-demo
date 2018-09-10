package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.HomeRecyclerAdapter;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.dto.BaseDto;
import com.cheersmind.cheersgenie.features.holder.BannerHomeHolder;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.CategoryDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ArticleRootEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 首页
 */
public class HomeFragment_bak extends LazyLoadFragment implements View.OnClickListener {

    //banner
    ConvenientBanner convenientBanner;

    //最新评测的标题
    TextView tvLastDimensionTitle;
    //继续最新评测的按钮
    Button btnGotoLastDimension;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    Unbinder unbinder;

    //banner内容集合
    List<String> bannerlist;
    Unbinder unbinder1;
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
            return new BannerHomeHolder(HomeFragment_bak.this, itemView);
        }

        @Override
        public int getLayoutId() {
            return R.layout.banneritem_home;
        }
    };

    //适配器的数据列表
    List<SimpleArticleEntity> recyclerItem;
    //适配器
    HomeRecyclerAdapter recyclerAdapter;
    //banner布局
    View headerBanner;
    //最新操作评测布局
    View headerEvalation;
    //空布局
    XEmptyLayout xemptyLayout;

    //下拉刷新的监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //加载banner
            loadBannerData();
            //加载最新操作的评测
            loadLastOperateEvaluation();
            //加载列表数据
            loadHomeDataList();
        }
    };
    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            loadHomeDataList();
        }
    };

    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    //底部滑出显示动画
//    private TranslateAnimation mShowAction;

    //recycler子项的点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            ToastUtil.showShort(getContext(), "点击第" + (position + 1) + "项");
            //跳转到文章详情页面
            String articleId = recyclerItem.get(position).getId();
            ArticleDetailActivity.startArticleDetailActivity(getContext(), articleId);
//            VideoActivity.startVideoActivity(getContext(), articleId);
        }
    };

    //recycler子项的孩子的点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener = new BaseQuickAdapter.OnItemChildClickListener() {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            switch (view.getId()) {
                //收藏
                case R.id.ibtn_favorite: {
                    SimpleArticleEntity simpleArticleEntity = recyclerItem.get(position);
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
    DimensionInfoEntity headDimension;


    @Override
    protected int setContentView() {
        return R.layout.fragment_home_bak;
    }

    @Override
    protected void onInitView(View contentView) {
        unbinder = ButterKnife.bind(this, contentView);

        //适配器
        recyclerAdapter = new HomeRecyclerAdapter(getContext(), R.layout.recycleritem_home, recyclerItem);
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
        //预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        recyclerAdapter.setPreLoadNumber(4);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //子项孩子的点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        //初始化header：banner
        headerBanner = getLayoutInflater().inflate(R.layout.recycler_header_home_banner, (ViewGroup) recycleView.getParent(), false);
        convenientBanner = headerBanner.findViewById(R.id.convenientBanner);
//        recyclerAdapter.addHeaderView(headerBanner);

        //初始化header：最新操作的评测
        headerEvalation = getLayoutInflater().inflate(R.layout.recycler_header_home_evaluation, (ViewGroup) recycleView.getParent(), false);
        tvLastDimensionTitle = headerEvalation.findViewById(R.id.tv_last_dimension_title);
        btnGotoLastDimension = headerEvalation.findViewById(R.id.btn_goto_last_dimension);
        btnGotoLastDimension.setOnClickListener(HomeFragment_bak.this);
//        recyclerAdapter.addHeaderView(headerEvalation);

    }

    @Override
    public void onResume() {
        super.onResume();
//        //开始自动翻页
        convenientBanner.startTurning(BANNER_AUTO_NEXT_PAGE_TIME);
    }

    @Override
    public void onPause() {
        super.onPause();
//        //停止翻页
        convenientBanner.stopTurning();
        //取消toast
        ToastUtil.cancelToast();
    }

    @Override
    protected void lazyLoad() {
        //加载banner
        loadBannerData();
        //加载最新操作的评测
        loadLastOperateEvaluation();
        //加载列表数据
        loadHomeDataList();
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
                try {
                    recyclerAdapter.removeHeaderView(headerBanner);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            @Override
            public void onResponse(Object obj) {
                //banner
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ArticleRootEntity articleRootEntity = InjectionWrapperUtil.injectMap(dataMap, ArticleRootEntity.class);
                    //文章集合
                    List<SimpleArticleEntity> dataList = articleRootEntity.getItems();
                    //非空
                    if (ArrayListUtil.isNotEmpty(dataList)) {
//                        bannerlist = Arrays.asList(images);
                        convenientBanner.setPages(viewHolderCreator, dataList) //mList是图片地址的集合
                                .setPointViewVisible(true)    //设置指示器是否可见
                                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                                //设置指示器位置（左、中、右）
                                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                                .setOnItemClickListener(bannerItemClickListener)//点击监听
                                .startTurning(BANNER_AUTO_NEXT_PAGE_TIME);     //设置自动切换（同时设置了切换时间间隔）
//                .setManualPageable(true)  //设置手动影响（设置了该项无法手动切换）
//        ;

                        recyclerAdapter.removeHeaderView(headerBanner);
                        recyclerAdapter.addHeaderView(headerBanner, 0);
//                recyclerAdapter.notifyDataSetChanged();

                    } else {
                        //没数据或者数据异常
                        throw new Exception();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        recyclerAdapter.removeHeaderView(headerBanner);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }

            }
        });
    }

    /**
     * 获取最新操作的评测
     */
    public void loadLastOperateEvaluation() {
        String defaultChildId = ChildInfoDao.getDefaultChildId();
        DataRequestService.getInstance().getLatestDimensionV2(defaultChildId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                try {
                    recyclerAdapter.removeHeaderView(headerEvalation);
                } catch (Exception err) {

                }
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    headDimension = InjectionWrapperUtil.injectMap(dataMap, DimensionInfoEntity.class);
                    if (headDimension != null && !TextUtils.isEmpty(headDimension.getDimensionId())) {
                        //刷新最新操作测评的视图
                        tvLastDimensionTitle.setText(headDimension.getDimensionName());

                    } else {
                        //没数据或者数据异常
                        throw new Exception();
                    }

                    recyclerAdapter.removeHeaderView(headerEvalation);
                    recyclerAdapter.addHeaderView(headerEvalation, 1);
//                recyclerAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    try {
                        recyclerAdapter.removeHeaderView(headerEvalation);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                    return;
                }

            }
        });
    }

    //获取首页数据列表（目前是热门文章）
    public void loadHomeDataList() {
        //设置空布局，当前列表还没有数据的情况，提示：通信等待提示中
        if (ArrayListUtil.isEmpty(recyclerItem)) {
            xemptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }
        if (swipeRefreshLayout.isRefreshing()) {
            //下拉刷新
            pageNum = 1;
            recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        }

        ArticleDto dto = new ArticleDto(pageNum, PAGE_SIZE);
        DataRequestService.getInstance().getArticles(dto, new BaseService.ServiceCallback() {
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

                } else {
                    //设置空布局：当前列表还没有数据的情况，提示：没有数据
                    if (ArrayListUtil.isEmpty(recyclerItem)) {
                        xemptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                    }
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
    }

    /**
     * 处理banner点击事件
     *
     * @param position
     */
    private void handlerBannerItemClick(int position) {
        ToastUtil.showShort(getContext(), "点击了第" + (position + 1) + "页");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_goto_last_dimension:
                ToastUtil.showShort(getContext(), "进入最新操作的评测页面");
                break;
        }
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
                    Boolean favorite = (Boolean) dataMap.get("favorite");
                    SimpleArticleEntity simpleArticleEntity = recyclerItem.get(position);
                    simpleArticleEntity.setFavorite(favorite);
                    int tempPosition = position + recyclerAdapter.getHeaderLayoutCount();
                    //局部数显列表项，把header计算在内
                    recyclerAdapter.notifyItemChanged(tempPosition);
//                    recyclerAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    //操作失败
                    e.printStackTrace();
                    onFailure(new QSCustomException("操作失败"));
                }
            }
        });
    }


    @OnClick(R.id.iv_category)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //种类
            case R.id.iv_category: {
                new CategoryDialog(getActivity(),null).show();
                break;
            }
        }
    }
}

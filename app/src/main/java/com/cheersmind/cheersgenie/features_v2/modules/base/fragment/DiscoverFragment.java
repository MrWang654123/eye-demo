package com.cheersmind.cheersgenie.features_v2.modules.base.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabViewPagerAdapter;
import com.cheersmind.cheersgenie.features.dto.BaseDto;
import com.cheersmind.cheersgenie.features.entity.CategoryEntity;
import com.cheersmind.cheersgenie.features.entity.CategoryRootEntity;
import com.cheersmind.cheersgenie.features.entity.HomeBanner;
import com.cheersmind.cheersgenie.features.entity.HomeBannerRootEntity;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.modules.article.activity.SearchArticleActivity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.CategoryDialog;
import com.cheersmind.cheersgenie.features.view.transformer.ScaleTransformer;
import com.cheersmind.cheersgenie.features_v2.adapter.HomeBannerPageAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.BannerDto;
import com.cheersmind.cheersgenie.features_v2.interfaces.MainTabListener;
import com.cheersmind.cheersgenie.features_v2.modules.discover.fragment.DiscoverTabItemFragment;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 发现
 */
public class DiscoverFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //banner
    @BindView(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    @BindView(R.id.viewPagerBanner)
    ViewPager viewPagerBanner;
    //首页banner viewPager适配器
    HomeBannerPageAdapter pageAdapter;

    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    //标签布局
    @BindView(R.id.tabs)
    TabLayout tabs;
    //内容viewpager
    @BindView(R.id.viewPager)
    ViewPager viewPager;


//    //banner子项点击监听
//    OnItemClickListener bannerItemClickListener = new OnItemClickListener() {
//        @Override
//        public void onItemClick(int position) {
//            handlerBannerItemClick(position);
//        }
//    };
//    //banner的ViewHolderCreator
//    CBViewHolderCreator viewHolderCreator = new CBViewHolderCreator() {
//        @Override
//        public Holder createHolder(View itemView) {
//            return new BannerHomeHolder(ExploreFragment.this, itemView);
//        }
//
//        @Override
//        public int getLayoutId() {
//            return R.layout.banneritem_home;
//        }
//    };


    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;


    //底部滑出显示动画
    private TranslateAnimation mShowAction;

    //banner翻页间隔时间
    private static final int BANNER_AUTO_NEXT_PAGE_TIME = 5000;

    //通信错误数量，目前本页面总共2个通信
    int errorQuantity = 0;
    //最大错误数量
    private final static int MAX_ERROR_QUANTITY = 2;
    //消息：错误数量
    private static final int MSG_ERROR_QUANTITY = 1;


    //banner文章集合
    List<HomeBanner> bannerArticleList;

    //分类集合数据
    private List<CategoryEntity> categories;

    //banner页之间的间距
    private int bannerPageMargin;


    @Override
    protected int setContentView() {
        return R.layout.fragment_discover;
    }

    @Override
    protected void onInitView(View contentView) {
        unbinder = ButterKnife.bind(this, contentView);

        //初始隐藏banner和tab模块
//        banner.setVisibility(View.GONE);
        convenientBanner.setVisibility(View.GONE);
        tabs.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        viewPagerBanner.setVisibility(View.GONE);

        //设置显示时的动画
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

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
                //加载分类
                loadCategory();
            }
        });

        //监听 AppBarLayout Offset 变化，动态设置 SwipeRefreshLayout 是否可用
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    //发送停止Fling的事件
                    EventBus.getDefault().post(new StopFlingEvent());
                }

            }
        });

        //动态计算banner的高度
//        final DisplayMetrics metrics = QSApplication.getMetrics();
//        int itemWidth = metrics.widthPixels;
//        final int itemHeight = (int) (itemWidth * (9f/16));
//        ViewTreeObserver vto = convenientBanner.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                convenientBanner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
////                int width = convenientBanner.getWidth();
////                final int resHeight = (int)(width * (310.0f / 398));
//
//                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) convenientBanner.getLayoutParams();
//                params.width = metrics.widthPixels;
//                params.height = itemHeight;
//                convenientBanner.setLayoutParams(params);
//            }
//        });

        //动态计算banner的高度
        final DisplayMetrics metrics = QSApplication.getMetrics();
        ViewTreeObserver vto = viewPagerBanner.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int margin = (int) getResources().getDimension(R.dimen.bannerHorizontalMargin);
                int bannerPageMarginExtra = (int) getResources().getDimension(R.dimen.bannerPageMarginExtra);
                viewPagerBanner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = metrics.widthPixels - margin * 2;
                final int resHeight = (int) ((width) * (9f/16));

                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) viewPagerBanner.getLayoutParams();
                params.width = width;
                params.height = resHeight;
                viewPagerBanner.setLayoutParams(params);

                //计算banner页之间的间距
//                int temp = DensityUtil.dip2px(getContext(), 33);
//                float density = getResources().getDisplayMetrics().density;
////                bannerPageMargin = -(int) (margin/2 * 3 + width*0.01);

//                bannerPageMargin = -((int) (margin/2 * 3) + bannerPageMarginExtra);
//                bannerPageMargin = ((int) (margin/2));
                bannerPageMargin =  -((int)(width * 0.13 - margin/5f*2));

//                ToastUtil.showLong(getContext(), "density：" + density + " margin：" + margin +" 屏宽：" + metrics.widthPixels + "  temp：" + temp + "  bannerPageMargin：" + bannerPageMargin);
//                System.out.println("页间距：" + bannerPageMargin);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
//        if (!ArrayListUtil.isEmpty(bannerArticleList)) {
//            //开始轮播
////            banner.startAutoPlay();
//            convenientBanner.startTurning();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
//        if (!ArrayListUtil.isEmpty(bannerArticleList)) {
//            //结束轮播
////            banner.stopAutoPlay();
//            convenientBanner.stopTurning();
//        }
        //取消toast
        ToastUtil.cancelToast();
    }


    @Override
    protected void lazyLoad() {
        //设置空布局，当前列表还没有数据的情况，提示：通信等待提示中
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //加载banner
        loadBannerData();
        //加载分类
        loadCategory();
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
        BannerDto dto = new BannerDto(1, 20);
        dto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());
        DataRequestService.getInstance().getHomeBanner(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
//                banner.setVisibility(View.GONE);
//                convenientBanner.setVisibility(View.GONE);
                viewPagerBanner.setVisibility(View.GONE);
                //发送通信错误消息
                getHandler().sendEmptyMessage(MSG_ERROR_QUANTITY);
            }

            @Override
            public void onResponse(Object obj) {
                //banner
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    HomeBannerRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, HomeBannerRootEntity.class);
                    //文章集合
                    bannerArticleList = rootEntity.getItems();
                    //非空
                    if (ArrayListUtil.isNotEmpty(bannerArticleList)) {
//                        bannerlist = Arrays.asList(images);
//                        List<String> images = new ArrayList<>();
//                        for (SimpleArticleEntity simpleArticle : bannerArticleList) {
//                            images.add(simpleArticle.getArticleImg());
//                        }

                        //设置图片加载器
                        /*banner.setImageLoader(new GlideImageLoader());
                        //设置图片集合
                        banner.setImages(images);
                        //banner设置方法全部调用完毕时最后调用
                        banner.start();
                        banner.setVisibility(View.VISIBLE);
                        banner.startAnimation(mShowAction);
                        */

//                        convenientBanner.setPages(viewHolderCreator, bannerArticleList) //mList是图片地址的集合
//                                .setPointViewVisible(true)    //设置指示器是否可见
//                                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
//                                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
//                                //设置指示器位置（左、中、右）
//                                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
//                                .setOnItemClickListener(bannerItemClickListener)//点击监听
//                                .startTurning(BANNER_AUTO_NEXT_PAGE_TIME);     //设置自动切换（同时设置了切换时间间隔）
////                .setManualPageable(true)  //设置手动影响（设置了该项无法手动切换）
////        ;
//                        convenientBanner.setVisibility(View.VISIBLE);
//                        convenientBanner.startAnimation(mShowAction);

                        if (pageAdapter == null) {
                            pageAdapter = new HomeBannerPageAdapter(DiscoverFragment.this, bannerArticleList);
                            //页面点击监听
                            pageAdapter.setListener(new HomeBannerPageAdapter.OnPageClickListener() {
                                @Override
                                public void onPageClick(int position) {
                                    handlerBannerItemClick(position);
                                }
                            });
                        }
                        viewPagerBanner.setVisibility(View.VISIBLE);
                        //滑动动画
                        viewPagerBanner.setPageTransformer(false, new ScaleTransformer());
                        //页面间距
//                        viewPagerBanner.setPageMargin(-DensityUtil.dip2px(getContext(),33));
                        viewPagerBanner.setPageMargin(bannerPageMargin);
                        //预加载数量（左右）
                        viewPagerBanner.setOffscreenPageLimit(2);
                        viewPagerBanner.setAdapter(pageAdapter);

                        //空布局：隐藏
//                        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    } else {
                        //没数据或者数据异常
                        throw new Exception();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    banner.setVisibility(View.GONE);
//                    convenientBanner.setVisibility(View.GONE);
                    viewPagerBanner.setVisibility(View.GONE);
                    //发送通信错误消息
                    getHandler().sendEmptyMessage(MSG_ERROR_QUANTITY);
                }

            }
        }, httpTag, getActivity());
    }


    /**
     * 加载分类
     */
    private void loadCategory() {
        DataRequestService.getInstance().getCategories(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                tabs.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                //发送通信错误消息
                getHandler().sendEmptyMessage(MSG_ERROR_QUANTITY);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CategoryRootEntity categoryRoot = InjectionWrapperUtil.injectMap(dataMap, CategoryRootEntity.class);
                    categories = categoryRoot.getItems();
                    //空的情况：默认选项“推荐”（加载所有文章）
                    if (ArrayListUtil.isEmpty(categories)) {
                        //没数据或者数据异常
//                        throw new Exception();
                    }

                    List<Pair<String, Fragment>> items = new ArrayList<>();
                    //第一项：推荐
//                    DiscoverRecommendFragment defaultFragment = new DiscoverRecommendFragment();
                    DiscoverTabItemFragment defaultFragment = new DiscoverTabItemFragment();
                    Bundle defaultBundle = new Bundle();
                    defaultBundle.putString(DiscoverTabItemFragment.HTTP_TAG, DiscoverTabItemFragment.class.getName() + "0");
                    defaultFragment.setArguments(defaultBundle);
                    items.add(new Pair<String, Fragment>("推荐",defaultFragment));
                    //有数据情况处理
                    for (int i=0; i<categories.size(); i++) {
                        final CategoryEntity categoryEntity = categories.get(i);
                        DiscoverTabItemFragment fragment = new DiscoverTabItemFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(DiscoverTabItemFragment.CATEGORY, categoryEntity);
                        bundle.putString(DiscoverTabItemFragment.HTTP_TAG, DiscoverTabItemFragment.class.getName() + (i + 1));
                        fragment.setArguments(bundle);
                        items.add(new Pair<String, Fragment>(categoryEntity.getName(),fragment));
                    }

                    tabs.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    viewPager.setAdapter(new TabViewPagerAdapter(getChildFragmentManager(), items));
                    //预加载左右2个
//                    viewPager.setOffscreenPageLimit(1);
                    //标签绑定viewpager
                    tabs.setupWithViewPager(viewPager);
//                    tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//                        @Override
//                        public void onTabSelected(TabLayout.Tab tab) {
//                            //定义方法，判断是否选中
//                            updateTabView(tab, true);
//                        }
//
//                        @Override
//                        public void onTabUnselected(TabLayout.Tab tab) {
//                            //定义方法，判断是否选中
//                            updateTabView(tab, false);
//                        }
//
//                        @Override
//                        public void onTabReselected(TabLayout.Tab tab) {
//                        }
//                    });

                    //如果如果categories数量为0，则隐藏tabs
                    if (categories.size() == 0) {
                        tabs.setVisibility(View.GONE);
                    } else {
                        //如果categories数量少于3个，则tabs置为fix
                        if (categories.size() < 3) {
                            tabs.setTabMode(TabLayout.MODE_FIXED);
                        } else {
                            tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
                        }
                    }

                    //设置分割线
                    LinearLayout linearLayout = (LinearLayout) tabs.getChildAt(0);
                    linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                    linearLayout.setDividerDrawable(ContextCompat.getDrawable(DiscoverFragment.this.getContext(),
                            R.drawable.tablayout_divider_line)); //设置分割线的样式
                    linearLayout.setDividerPadding(DensityUtil.dip2px(DiscoverFragment.this.getContext(),16)); //设置分割线间隔

                    //空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                } catch (Exception e) {
                    e.printStackTrace();
                    tabs.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                    //发送通信错误消息
                    getHandler().sendEmptyMessage(MSG_ERROR_QUANTITY);
                }
            }
        }, httpTag, getActivity());
    }


    /**
     * 处理banner点击事件
     *
     * @param position 索引
     */
    private void handlerBannerItemClick(int position) {
        HomeBanner homeBanner = bannerArticleList.get(position);
        //文章
        if (homeBanner.getType() == 1) {
            //跳转文章详情
            String articleId = homeBanner.getValue();
            String ivMainUrl = homeBanner.getImg_url();
            String articleTitle = homeBanner.getDescribe();
            ArticleDetailActivity.startArticleDetailActivity(getContext(), articleId, ivMainUrl, articleTitle);

        } else if (homeBanner.getType() == 2) {//tab
            //跳转到指定位置的tab
            int tabPosition = Integer.valueOf(homeBanner.getValue());
            try {
                if (getActivity() instanceof MainTabListener) {
                    ((MainTabListener) getActivity()).goTab(tabPosition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @OnClick({R.id.iv_category, R.id.iv_search, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //分类
            case R.id.iv_category: {
                new CategoryDialog(getActivity(), null).show();
                break;
            }
            //搜索
            case R.id.iv_search: {
                SearchArticleActivity.startSearchArticleActivity(getContext(), null);
                break;
            }
            //FAB
            case R.id.fab: {
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), "点击FAB");
                }
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

}
